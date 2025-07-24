package com.azane.ogna.capability.skill;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.network.to_client.SyncWeaponCapPacket;
import com.azane.ogna.registry.ModAttributes;
import com.azane.ogna.resource.service.CommonDataService;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OgnaSkillCap implements ISkillCap
{
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

    private AttrMap baseAttrMap = new AttrMap(Attributes.ATTACK_DAMAGE);
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
        double need = weaponCap.submitAttrVal(ModAttributes.SKILL_SP.get(), player, stack, skill.getSkillData().getSP());
        return need <= SP;
    }

    @Override
    public void start(Level level, Player player, ItemStack stack)
    {
        if(skill == null)
            return;
        double consume = weaponCap.submitAttrVal(ModAttributes.SKILL_SP.get(),player,stack,skill.getSkillData().getSP());
        double duration = weaponCap.submitAttrVal(ModAttributes.SKILL_DURATION.get(),player,stack,skill.getSkillData().getDuration());
        SP = Mth.clamp(SP - consume, 0, Double.MAX_VALUE);
        RD = Mth.clamp(duration,0,Double.MAX_VALUE);
        active = true;
        skill.onSkillStart(level,player,(IOgnaWeapon) stack.getItem(),stack);
    }

    @Override
    public void end(Level level, Player player, ItemStack stack)
    {
        active = false;
        RD = 0;
        if(skill != null)
            skill.onSkillEnd(level, player, (IOgnaWeapon) stack.getItem(), stack);
    }

    @Override
    public void equipSkill(ResourceLocation rl)
    {
        unequipSkill();
        skillRL = rl;
        skill = CommonDataService.get().getSkill(skillRL);
        if(skill != null)
        {
            skill.getSkillData().getBaseAttrModifiers().forEach(am->{
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(am.getAttribute());
                if (attribute != null)
                    baseAttrMap.getAttribute(attribute).acceptModifier(am);
            });
            skill.getSkillData().getSkillAttrModifiers().forEach(am->{
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(am.getAttribute());
                if (attribute != null)
                    skillAttrMap.getAttribute(attribute).acceptModifier(am);
            });
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
    public void modifySP(double val, boolean needSync, Player player, ItemStack stack)
    {
        if(skill == null)
            return;
        SP = Mth.clamp(SP + val, 0, skill.getSkillData().getStorage()*weaponCap.submitAttrVal(ModAttributes.SKILL_SP.get(),player, stack,skill.getSkillData().getSP()));
        if(!needSync || player == null || stack == null)
            return;
        if(player instanceof ServerPlayer serverPlayer)
            SyncWeaponCapPacket.trySend(serverPlayer,stack, SyncWeaponCapPacket.CapData.SKILL_SP, SP);
    }

    @Override
    public void modifyRD(double val, boolean needSync, Player player, ItemStack stack)
    {
        if(skill == null)
            return;
        RD = Mth.clamp(RD + val, 0, weaponCap.submitAttrVal(ModAttributes.SKILL_DURATION.get(),player, stack,skill.getSkillData().getDuration()));
        if(!needSync || player == null || stack == null)
            return;
        if(player instanceof ServerPlayer serverPlayer)
            SyncWeaponCapPacket.trySend(serverPlayer,stack, SyncWeaponCapPacket.CapData.SKILL_RD, RD);
    }

    @Override
    public AttrMap.Matrices extractBaseMatrices(Set<Attribute> requirement)
    {
        return baseAttrMap.extractMatrices(requirement);
    }

    @Override
    public AttrMap.Matrices extractSkillMatrices(Set<Attribute> requirement)
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
        baseAttrMap.deserializeNBT(nbt.getCompound("baseAttrMap"));
        skillAttrMap.deserializeNBT(nbt.getCompound("skillAttrMap"));
    }
}
