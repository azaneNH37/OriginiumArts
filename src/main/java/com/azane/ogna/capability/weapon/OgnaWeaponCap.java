package com.azane.ogna.capability.weapon;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.skill.OgnaSkillCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.attr.AttrMatrix;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipEnv;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.combat.data.weapon.OgnaWeaponData;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.network.to_client.SyncWeaponCapPacket;
import com.azane.ogna.registry.ModAttribute;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

//TODO: 注意C/S端数据同步！
/**
 * @author azaneNH37 (2025-08-09)
 */
public class OgnaWeaponCap implements IOgnaWeaponCap
{
    //不需要持久化，因为每次加载时都会刷进来
    private OgnaWeaponData baseData;
    @Getter
    private ISkillCap skillCap;
    @Getter
    private ChipSet chipSet;
    @Getter
    private double currentEnergy = 100;

    private AttrMap attrMap = new AttrMap(Attributes.ATTACK_DAMAGE);

    public OgnaWeaponCap(OgnaWeaponData baseData,@Nullable CompoundTag storedData)
    {
        this.baseData = baseData;
        this.skillCap = new OgnaSkillCap(this);
        this.chipSet = new ChipSet(ChipEnv.WEAPON);
        if(storedData == null)
        {
            baseData.getAttrModifiers().forEach(attrMap::acceptModifier);
            //事实上，这里传空chipArg是符合预期的行为，因为此处的chip属性是跟武器data走的，不应在insert处因stack和所有者而异（期待回旋镖）
            baseData.getInnerChips().stream().map(OgnaChip::getChip).forEach(i->this.chipSet.insertChip(i,ChipArg.EMPTY));
            currentEnergy = submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_STORE.get(), null, null);
        }
        else
        {
            if(storedData.contains("Parent"))
                this.deserializeNBT(storedData.getCompound("Parent"));
            else
                this.deserializeNBT(storedData);
        }

    }

    @Override
    public void acceptModifier(AttrModifier modifier) {attrMap.acceptModifier(modifier);}

    @Override
    public void removeModifier(AttrModifier modifier) {attrMap.removeModifier(modifier);}

    @Override
    public boolean canAttack(ItemStack stack, Player player, AttackType attackType)
    {
        return baseData.isCanAttack() && submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_CONSUME.get(),player,stack) <= getCurrentEnergy();
    }

    @Override
    public boolean canReload(ItemStack stack, Player player)
    {
        return baseData.isCanReload() && submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_STORE.get(),player,stack) > getCurrentEnergy();
    }

    @Override
    public double getBaseAttrVal(Attribute attribute, ItemStack stack)
    {
        return baseData.getBaseValue(attribute);
    }

    @Override
    public double submitAttrVal(Attribute attribute, @Nullable Player player, ItemStack stack, double baseValue)
    {
        return AttrMatrix.combine(true,
            attrMap.getAttribute(attribute).extractMatrix(),
            skillCap.getBaseAttrMap().getAttribute(attribute).extractMatrix(),
            skillCap.isActive() ? skillCap.getSkillAttrMap().getAttribute(attribute).extractMatrix() : null
            ).submit(baseValue);
    }

    @Override
    public AttrMap.Matrices extractMatrices(Set<Attribute> requirement)
    {
        return AttrMap.Matrices.combine(
            attrMap.extractMatrices(requirement),
            skillCap.extractBaseMatrices(requirement),
            skillCap.isActive() ? skillCap.extractSkillMatrices(requirement) : null
            );
    }

    @Override
    public void modifyCurrentEnergy(double val, boolean needSync, Player player, ItemStack stack)
    {
        currentEnergy = Mth.clamp(currentEnergy + val, 0, submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_STORE.get(),player, stack));
        if(!needSync || player == null || stack == null)
            return;
        if(player instanceof ServerPlayer serverPlayer)
            SyncWeaponCapPacket.trySend(serverPlayer,stack, SyncWeaponCapPacket.CapData.CURRENT_ENERGY, currentEnergy);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var nbt = new CompoundTag();
        nbt.put("attrMap", attrMap.serializeNBT());
        nbt.put("skillCap", skillCap.serializeNBT());
        nbt.putDouble("currentEnergy", currentEnergy);
        nbt.put("chipSet", chipSet.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        attrMap.deserializeNBT((CompoundTag) nbt.get("attrMap"));
        skillCap.deserializeNBT((CompoundTag) nbt.get("skillCap"));
        currentEnergy = nbt.getDouble("currentEnergy");
        chipSet.deserializeNBT(nbt.getCompound("chipSet"));
    }
}
