package com.azane.ogna.capability.weapon;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.item.weapon.AttackType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IOgnaWeaponCap extends INBTSerializable<CompoundTag>
{
    IOgnaWeaponCap FALLBACK = new IOgnaWeaponCap()
    {
        @Override
        public ISkillCap getSkillCap() { return ISkillCap.FALLBACK; }

        @Override
        public ChipSet getChipSet() { return ChipSet.FALLBACK;}

        @Override
        public void acceptModifier(AttrModifier modifier) {}

        @Override
        public void removeModifier(AttrModifier modifier) {}

        @Override
        public boolean canAttack(ItemStack stack, Player player, AttackType attackType) {return false;}

        @Override
        public boolean canReload(ItemStack stack, Player player) {return false;}

        @Override
        public double getBaseAttrVal(Attribute attribute, ItemStack stack) {return Double.NaN;}

        @Override
        public double submitAttrVal(Attribute attribute, @Nullable Player player, ItemStack stack, double baseValue) {return Double.NaN;}

        @Override
        public AttrMap.Matrices extractMatrices(Set<Attribute> requirement)
        {
            return null;
        }

        @Override
        public double getCurrentEnergy() {return 0;}

        @Override
        public void modifyCurrentEnergy(double val, boolean needSync, Player player, ItemStack stack) {}

        @Override
        public CompoundTag serializeNBT(){return new CompoundTag(); }

        @Override
        public void deserializeNBT(CompoundTag nbt){}
    };

    ISkillCap getSkillCap();

    ChipSet getChipSet();

    void acceptModifier(AttrModifier modifier);

    void removeModifier(AttrModifier modifier);

    boolean canAttack(ItemStack stack, Player player, AttackType attackType);

    boolean canReload(ItemStack stack, Player player);

    double getBaseAttrVal(Attribute attribute,ItemStack stack);

    double submitAttrVal(Attribute attribute, @Nullable Player player, ItemStack stack,double baseValue);

    default double submitBaseAttrVal(Attribute attribute,@Nullable Player player, ItemStack stack)
    {
        return submitAttrVal(attribute,player,stack,getBaseAttrVal(attribute,stack));
    }

    AttrMap.Matrices extractMatrices(Set<Attribute> requirement);

    double getCurrentEnergy();

    void modifyCurrentEnergy(double val, boolean needSync, Player player, ItemStack stack);
}