package com.azane.ogna.capability.weapon;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.skill.OgnaSkillCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.attr.AttrMatrix;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipEnv;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.combat.data.OgnaWeaponData;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.NbtHelper;
import com.azane.ogna.network.to_client.SyncWeaponCapPacket;
import com.azane.ogna.registry.ModAttribute;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//TODO: 注意C/S端数据同步！
/**
 * @author azaneNH37 (2025-08-09)
 */
public class OgnaWeaponCap implements IOgnaWeaponCap
{
    private static final String VERSION_TAG = "ognmarts.version";
    //不需要持久化，因为每次加载时都会刷进来
    private OgnaWeaponData baseData;
    @Getter
    private ISkillCap skillCap;
    @Getter
    private ChipSet chipSet;
    @Getter
    private double currentEnergy = 100;

    private AttrMap attrMap = new AttrMap(Attributes.ATTACK_DAMAGE);

    /**
     * 一定要注意检查子方法中会不会涉及getCapability的循环调用
     * @param weapon
     * @param storedData
     */
    public OgnaWeaponCap(ItemStack weapon,@Nullable CompoundTag storedData)
    {
        if(!IOgnaWeapon.isWeapon(weapon))
            throw new IllegalArgumentException("ItemStack should be a valid ogna weapon stack when generating capabilities: "+weapon);
        IOgnaWeapon iWeapon = (IOgnaWeapon)weapon.getItem();
        this.baseData = iWeapon.getDefaultDatabase(weapon).getOgnaWeaponData();
        this.skillCap = new OgnaSkillCap(this);
        this.chipSet = new ChipSet(ChipEnv.WEAPON);
        if(storedData == null)
        {
            baseData.getAttrModifiers().forEach(attrMap::acceptModifier);
            //回旋镖了（蚌）
            baseData.getInnerChips().stream().map(OgnaChip::getChip).forEach(i->this.chipSet.insertChip(i,ChipArg.of(null,null,this)));
            currentEnergy = submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_STORE.get(), null, null);
            NbtHelper.put(weapon.getOrCreateTag(), VERSION_TAG, OriginiumArts.VERSION);
        }
        else
        {
            boolean isVersionMatch = versionCheck(weapon);
            if(storedData.contains("Parent"))
                this.deserializeNBT(storedData.getCompound("Parent"));
            else
                this.deserializeNBT(storedData);
            if(!isVersionMatch)
            {
                Marker marker = MarkerManager.getMarker("OgnaWeaponCap");
                DebugLogger.warn(marker,
                    "Stack {}  Weapon capability version mismatch! Expected: {}, Found: {}. Reinitializing the pre-built data.",
                    weapon,OriginiumArts.VERSION,
                    NbtHelper.get(weapon.getOrCreateTag(), VERSION_TAG,String.class));
                NbtHelper.put(weapon.getOrCreateTag(), VERSION_TAG, OriginiumArts.VERSION);
                List<IChip> chips = chipSet.getStoredChips(i->true);
                attrMap = new AttrMap(Attributes.ATTACK_DAMAGE);
                chipSet = new ChipSet(ChipEnv.WEAPON);
                baseData.getAttrModifiers().forEach(attrMap::acceptModifier);
                chips.forEach(chip -> chipSet.insertChip(chip, ChipArg.of(null, null, this)));
                if(skillCap.getSkill() != null)
                {
                    DebugLogger.warn(marker,
                        "Reapplying skill {} to weapon stack {}",
                        skillCap.getSkill().getId(), weapon);
                    ResourceLocation skillRl = skillCap.getSkill().getId();
                    skillCap.equipSkill(skillRl);
                }
                // 及时把数据刷回去，很神秘，只有这样才能保证持久化
                storedData.put("Parent", this.serializeNBT());
            }
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

    /**
     * 此处绝对不能调用stack的getCapability，因为会导致死循环<br>
     * 不过话又说回来了，谁会在cap里面再找自己
     * @param attribute
     * @param player
     * @param stack
     * @param baseValue
     * @return
     */
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
        Optional.ofNullable(nbt.get("attrMap")).map(CompoundTag.class::cast).ifPresent(attrMap::deserializeNBT);
        Optional.ofNullable(nbt.get("skillCap")).map(CompoundTag.class::cast).ifPresent(skillCap::deserializeNBT);
        currentEnergy = nbt.getDouble("currentEnergy");
        Optional.ofNullable(nbt.get("chipSet")).map(CompoundTag.class::cast).ifPresent(chipSet::deserializeNBT);
    }

    private boolean versionCheck(ItemStack stack)
    {
        String recordVersion = NbtHelper.getOrCreate(stack.getOrCreateTag(),VERSION_TAG,OriginiumArts.VERSION);
        return OriginiumArts.VERSION.equals(recordVersion);
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        if(getSkillCap().getSkill() != null)
        {
            tooltip.add(Component.translatable("ogna.tip.weapon.cap.skill").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC));
            getSkillCap().getSkill().appendHoverText(stack,tooltip, flag);
            tooltip.add(Component.empty());
        }
        if(!chipSet.isEmpty())
        {
            tooltip.add(Component.translatable("ogna.tip.weapon.cap.chipset").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC));
            chipSet.appendHoverText(stack,tooltip,flag);
            tooltip.add(Component.empty());
        }
    }
}
