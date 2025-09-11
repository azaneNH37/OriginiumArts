package com.azane.ogna.capability.skill;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.util.AttrMatrixHelper;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.ModUtil;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * @author azaneNH37 (2025-08-02)
 */
public class OgnaSkillCap implements ISkillCap
{
    @NotNull
    private IOgnaWeaponCap weaponCap;

    private boolean active;
    public boolean isActive()
    {
        return active && skill != null;
    }

    @Getter
    @Nullable
    private ISkill skill;
    @Nullable
    private ResourceLocation skillRL;
    @Getter
    private double SP;
    @Getter
    private double RD;

    @Getter
    private AttrMap baseAttrMap = new AttrMap(Attributes.ATTACK_DAMAGE);
    @Getter
    private AttrMap skillAttrMap = new AttrMap(Attributes.ATTACK_DAMAGE);

    public OgnaSkillCap(IOgnaWeaponCap weaponCap)
    {
        this.weaponCap = weaponCap;
    }

    @Override
    public boolean canStart(Level level, Player player, ItemStack stack)
    {
        if(skill == null || active)
            return false;
        double need = AttrMatrixHelper.commonSubmit(ModAttribute.SKILL_SP.get(),skill.getSkillData().getSP(),weaponCap);
        //weaponCap.submitAttrVal(, player, stack, skill.getSkillData().getSP());
        return need <= SP;
    }

    @Override
    public void start(Level level, Player player, ItemStack stack)
    {
        if(skill == null)
            return;
        double consume = AttrMatrixHelper.commonSubmit(ModAttribute.SKILL_SP.get(),skill.getSkillData().getSP(),weaponCap);
            //weaponCap.submitAttrVal(ModAttribute.SKILL_SP.get(),player,stack,skill.getSkillData().getSP());
        double duration = AttrMatrixHelper.commonSubmit(ModAttribute.SKILL_DURATION.get(),skill.getSkillData().getDuration(),weaponCap);
            //weaponCap.submitAttrVal(ModAttribute.SKILL_DURATION.get(),player,stack,skill.getSkillData().getDuration());
        SP = Mth.clamp(SP - consume, 0, Double.MAX_VALUE);
        RD = Mth.clamp(duration,0,Double.MAX_VALUE);
        active = true;
        skill.onSkillStart(level,player,(IOgnaWeapon) stack.getItem(),stack);
        weaponCap.getChipSet().gather(ChipTiming.ON_SKILL_START).forEach(chip -> chip.onSkillStart(level, player, stack, weaponCap, this));
    }

    @Override
    public void end(Level level, Player player, ItemStack stack)
    {
        active = false;
        RD = 0;
        if(skill != null)
        {
            skill.onSkillEnd(level, player, (IOgnaWeapon) stack.getItem(), stack);
            weaponCap.getChipSet().gather(ChipTiming.ON_SKILL_END).forEach(chip -> chip.onSkillEnd(level, player, stack, weaponCap, this));
        }
    }

    @Override
    public void equipSkill(ResourceLocation rl)
    {
        unequipSkill();
        skillRL = rl;
        skill = CommonDataService.get().getSkill(skillRL);
        if(skill != null)
        {
            skill.getSkillData().getBaseAttrModifiers().forEach(baseAttrMap::acceptModifier);
            skill.getSkillData().getSkillAttrModifiers().forEach(skillAttrMap::acceptModifier);
        }
    }

    @Override
    public void unequipSkill()
    {
        skillRL = null;
        skill = null;
        SP = 0;
        RD = 0;
        baseAttrMap = new AttrMap(Attributes.ATTACK_DAMAGE);
        skillAttrMap = new AttrMap(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void modifySP(double val, boolean needSync, Player player,@NotNull ItemStack stack)
    {
        if(skill == null)
            return;
        SP = Mth.clamp(SP + val, 0, skill.getSkillData().getStorage()*AttrMatrixHelper.commonSubmit(ModAttribute.SKILL_SP.get(),skill.getSkillData().getSP(),weaponCap));
    }

    @Override
    public void modifyRD(double val, boolean needSync, Player player,@NotNull ItemStack stack)
    {
        if(skill == null)
            return;
        RD = Mth.clamp(RD + val, 0, AttrMatrixHelper.commonSubmit(ModAttribute.SKILL_DURATION.get(),skill.getSkillData().getDuration(),weaponCap));
    }

    @Override
    public AttrMap.Matrices extractBaseMatrices(Iterable<Attribute> requirement)
    {
        return baseAttrMap.extractMatrices(requirement);
    }

    @Override
    public AttrMap.Matrices extractSkillMatrices(Iterable<Attribute> requirement)
    {
        return skillAttrMap.extractMatrices(requirement);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var tag = new CompoundTag();
        if(skillRL != null)
            tag.putString("skill", skillRL.toString());
        tag.putDouble("SP", SP);
        tag.putDouble("RD", RD);
        tag.putBoolean("active", active);
        tag.put("baseAttrMap", baseAttrMap.serializeNBT());
        tag.put("skillAttrMap", skillAttrMap.serializeNBT());
        return tag;
    }

    @Override
    public CompoundTag serializeSyncNBT()
    {
        var tag = new CompoundTag();
        if(skillRL != null)
            tag.putString("skill", skillRL.toString());
        tag.putDouble("SP", SP);
        tag.putDouble("RD", RD);
        tag.putBoolean("active", active);
        tag.put("baseAttrMap", baseAttrMap.serializeNBTFiltered(ModUtil.SYNC_ATTRIBUTES));
        tag.put("skillAttrMap", skillAttrMap.serializeNBTFiltered(ModUtil.SYNC_ATTRIBUTES));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if(nbt.contains("skill"))
        {
            skillRL = ResourceLocation.tryParse(nbt.getString("skill"));
            skill = CommonDataService.get().getSkill(skillRL);
        }
        SP = nbt.getDouble("SP");
        RD = nbt.getDouble("RD");
        active = nbt.getBoolean("active");
        Optional.ofNullable(nbt.get("baseAttrMap")).map(CompoundTag.class::cast).ifPresent(baseAttrMap::deserializeNBT);
        Optional.ofNullable(nbt.get("skillAttrMap")).map(CompoundTag.class::cast).ifPresent(skillAttrMap::deserializeNBT);
    }
}
