package com.azane.ogna.item.weapon;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.client.lib.IDynamicAssetItem;
import com.azane.ogna.client.lib.IOffHandItem;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.registry.ModSound;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-09)
 */
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
        if(pEntity instanceof Player)
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
        skillCap.modifySP(getWeaponCap(stack).submitAttrVal(ModAttribute.SKILL_SP_RATE.get(),player,stack,1D),false,player,stack);
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
            if(level instanceof ServerLevel serverLevel)
            {
                SoundKeyData.getSound(ModSound.SKILL_START_UNIT).ifPresent(soundEvent ->
                    serverLevel.playSound(null,
                        player.position().x, player.position().y, player.position().z,
                        soundEvent, SoundSource.PLAYERS, ModSound.SKILL_START_UNIT.getVolume(), ModSound.SKILL_START_UNIT.getPitch())
                    );
            }
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
        //TODO:太神秘了（
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        GeoItem.getOrAssignId(stack,server.getLevel(Level.OVERWORLD));
        this.getOrCreateStackUUID(stack);
        return stack;
    }
}
