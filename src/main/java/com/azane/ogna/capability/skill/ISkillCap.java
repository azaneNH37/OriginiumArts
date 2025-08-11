package com.azane.ogna.capability.skill;

import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.genable.item.skill.ISkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 含义为贴附在武器上的一个Skill，所以其应当在skill修改时被重新初始化
 * @author azaneNH37 (2025-07-25)
 */
public interface ISkillCap extends INBTSerializable<CompoundTag>
{
    ISkillCap FALLBACK = new ISkillCap()
    {
        @Override
        public boolean isActive() {return false;}

        @Override
        public boolean canStart(Level level, Player player, ItemStack stack) {return false;}

        @Override
        public void start(Level level, Player player, ItemStack stack) {}

        @Override
        public void end(Level level, Player player, ItemStack stack) {}

        @Override
        public ISkill getSkill() { return null; }

        @Override
        public void equipSkill(ResourceLocation rl) {}

        @Override
        public void unequipSkill() {}

        @Override
        public double getSP() { return 0; }

        @Override
        public double getRD() { return 0; }

        @Override
        public void modifySP(double val, boolean needSync, Player player, ItemStack stack) {}

        @Override
        public void modifyRD(double val, boolean needSync, Player player, ItemStack stack) {}

        @Override
        public AttrMap.Matrices extractBaseMatrices(Set<Attribute> requirement) {return null;}

        @Override
        public AttrMap.Matrices extractSkillMatrices(Set<Attribute> requirement) {return null;}

        @Override
        public AttrMap getBaseAttrMap()
        {
            return null;
        }

        @Override
        public AttrMap getSkillAttrMap()
        {
            return null;
        }

        @Override
        public CompoundTag serializeNBT() { return new CompoundTag(); }

        @Override
        public void deserializeNBT(CompoundTag nbt) {}
    };

    boolean isActive();

    boolean canStart(Level level, Player player, ItemStack stack);

    void start(Level level,Player player, ItemStack stack);

    void end(Level level,Player player,ItemStack stack);

    @Nullable
    ISkill getSkill();

    void equipSkill(ResourceLocation rl);

    void unequipSkill();

    double getSP();

    double getRD();

    void modifySP(double val, boolean needSync, Player player, ItemStack stack);

    void modifyRD(double val, boolean needSync, Player player, ItemStack stack);

    AttrMap.Matrices extractBaseMatrices(Set<Attribute> requirement);

    AttrMap.Matrices extractSkillMatrices(Set<Attribute> requirement);

    AttrMap getBaseAttrMap();
    AttrMap getSkillAttrMap();
}
