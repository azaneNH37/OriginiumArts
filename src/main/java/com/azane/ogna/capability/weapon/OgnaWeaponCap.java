package com.azane.ogna.capability.weapon;

import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.data.OgnaWeaponData;
import com.azane.ogna.item.genable.AttackType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

//TODO: 注意C/S端数据同步！
public class OgnaWeaponCap implements IOgnaWeaponCap
{
    //不需要持久化，因为每次加载时都会刷进来
    private OgnaWeaponData baseData;

    private AttrMap attrMap = new AttrMap(Attributes.ATTACK_DAMAGE);

    public OgnaWeaponCap(OgnaWeaponData baseData,@Nullable CompoundTag storedData)
    {
        this.baseData = baseData;
        if(storedData == null)
            baseData.getAttrModifiers().forEach(attrModifier -> {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attrModifier.getAttribute());
                if (attribute != null)
                    attrMap.getAttribute(attribute).acceptModifier(attrModifier);
            });
        else
            attrMap.deserializeNBT(storedData);
    }

    @Override
    public boolean canAttack(ItemStack stack, Player player, AttackType attackType)
    {
        return baseData.isCanAttack() && baseData.getConsumption() <= getCurrentEnergy(stack);
    }

    @Override
    public boolean canReload(ItemStack stack, Player player)
    {
        return baseData.isCanReload() && baseData.getMaxEnergy() > getCurrentEnergy(stack);
    }

    @Override
    public double getBaseAttrVal(Attribute attribute, ItemStack stack)
    {
        return baseData.getBaseValue(attribute);
    }

    @Override
    public double submitAttrVal(Attribute attribute, @Nullable Player player, ItemStack stack, double baseValue)
    {
        return attrMap.getAttribute(attribute).extractMatrix().submit(baseValue);
    }

    @Override
    public AttrMap.Matrices extractMatrices(Set<Attribute> requirement)
    {
        return attrMap.extractMatrices(requirement);
    }

    @Override
    public double getCurrentEnergy(ItemStack stack)
    {
        return 100;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var nbt = new CompoundTag();
        nbt.put("attrMap", attrMap.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        attrMap.deserializeNBT((CompoundTag) nbt.get("attrMap"));
    }
}
