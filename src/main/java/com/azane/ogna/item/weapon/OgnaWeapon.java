package com.azane.ogna.item.weapon;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.client.lib.IDynamicAssetItem;
import com.azane.ogna.client.lib.IOffHandItem;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.skill.ISkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;

public abstract class OgnaWeapon extends Item implements GeoItem, IOffHandItem, IDynamicAssetItem, IGenItem,IOgnaWeapon
{
    public OgnaWeapon(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal(this.getStackUUID(pStack)));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected)
    {
        tick(pLevel, (Player) pEntity, pStack);
    }

    //C/S同步触发
    @Override
    public void tick(Level level, Player player, ItemStack stack)
    {
        ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
        ISkill skill = skillCap.getSkill();
        if(skill != null)
        {
            skill.onSkillTick(level,player,this,stack,skillCap.isActive());
            if (skillCap.isActive())
                tickDuration(level, player, stack);
            else
                tickSP(level, player, stack);
        }
    }

    @Override
    public void tickDuration(Level level, Player player, ItemStack stack)
    {
         ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
         skillCap.modifyRD(-1D,false,player,stack);
        //DebugLogger.log("Weapon %s ticked RD, now is %s".formatted(this.getStackUUID(stack), skillCap.getRD()));
        if(skillCap.getRD() <= 0)
             skillCap.end(level,player,stack);
    }

    @Override
    public void tickSP(Level level, Player player, ItemStack stack)
    {
        ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
        skillCap.modifySP(1D,false,player,stack);
        //DebugLogger.log("Weapon %s ticked SP, now is %s".formatted(this.getStackUUID(stack), skillCap.getSP()));
    }

    @Override
    public void onItemStackTransform()
    {

    }

    @Override
    public void onSkillEquip(ItemStack stack, ResourceLocation rl)
    {
        ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
        skillCap.equipSkill(rl);
    }

    @Override
    public void onSkillUnequip(ItemStack stack)
    {
        ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
        skillCap.unequipSkill();
    }

    @Override
    public boolean onSkillInvoke(Level level, Player player, ItemStack stack)
    {
        ISkillCap skillCap = getWeaponCap(stack).getSkillCap();
        if (skillCap.canStart(level, player, stack))
        {
            skillCap.start(level, player, stack);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable ItemStack templateBuildItemStack(CompoundTag tag, int count)
    {
        ItemStack stack = IGenItem.super.templateBuildItemStack(tag, count);
        if(stack == null)
            return null;
        this.getOrCreateStackUUID(stack);
        return stack;
    }
}
