package com.azane.ogna.genable.item.skill;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.data.skill.OgnaSkillData;
import com.azane.ogna.combat.util.CombatFirer;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.data.display.SkillDisplayContext;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-11)
 */
@Getter
@JsonClassTypeBinder(fullName = "skill.default",simpleName = "sk.d",namespace = OriginiumArts.MOD_ID)
public class DefaultSkillDataBase implements ISkill
{
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;
    @SerializedName("display_context")
    private SkillDisplayContext displayContext = new SkillDisplayContext();
    @SerializedName("level")
    private int skillLevel;
    @SerializedName("base_data")
    private OgnaSkillData skillData;
    @SerializedName("attack_entities")
    private AtkEntityData atkEntities;
    @SerializedName("prefix")
    private ResourceLocation prefixSkill = RlHelper.EMPTY;
    @SerializedName("suffix")
    private ResourceLocation suffixSkill = RlHelper.EMPTY;


    @Override
    public void onSkillTick(Level level, Player player, IOgnaWeapon weapon, ItemStack stack, boolean isOpen)
    {

    }

    @Override
    public void onSkillStart(Level level, Player player, IOgnaWeapon weapon, ItemStack stack)
    {
        DebugLogger.log("Skill %s started for player %s".formatted(id, player.getName().getString()));
    }

    @Override
    public void onSkillEnd(Level level, Player player, IOgnaWeapon weapon, ItemStack stack)
    {
        DebugLogger.log("Skill %s ended for player %s".formatted(id, player.getName().getString()));
    }

    @Override
    public boolean onServerAttack(ServerLevel level, ServerPlayer player, IOgnaWeapon weapon, ItemStack stack, AttackType attackType, long chargeTime, boolean isOpen)
    {
        DebugLogger.log("Skill %s executed by player %s with attack type %s".formatted(id, player.getName().getString(), attackType));
        CombatFirer.fireDefault(level,player,weapon,weapon.getWeaponCap(stack),stack,"skill","skill");
        return true;
    }

    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
    }

    @Override
    public void registerDataBase()
    {
        Item item = ModItem.OGNA_SKILL.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(ISkill.class).registerDataBase(this);
        }
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ModItem.OGNA_SKILL.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.translatable(displayContext.getName()).withStyle(Style.EMPTY.withColor(displayContext.getColor())));
        tooltip.add(Component.translatable(displayContext.getDescription()).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("attribute.name.ognmarts.skill.sp").append(" %d".formatted(skillData.getSP()/10)).withStyle(ChatFormatting.GREEN,ChatFormatting.BOLD)
                .append("  ")
                .append(Component.translatable("attribute.name.ognmarts.skill.duration").append(" %ds".formatted(skillData.getDuration()/10)).withStyle(ChatFormatting.GOLD,ChatFormatting.BOLD)));
        tooltip.add(Component.empty());
        appendSkillDetailHoverText(stack,tooltip,flag);
        tooltip.add(Component.empty());
        if(!skillData.getBaseAttrModifiers().isEmpty())
            tooltip.add(Component.translatable("ogna.tip.skill.base.attr").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        skillData.getBaseAttrModifiers().forEach(attrModifier -> attrModifier.appendHoverText(stack,tooltip,flag));
        if(!skillData.getSkillAttrModifiers().isEmpty())
            tooltip.add(Component.translatable("ogna.tip.skill.skill.attr").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        skillData.getSkillAttrModifiers().forEach(attrModifier -> attrModifier.appendHoverText(stack,tooltip,flag));

    }

    public void appendSkillDetailHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {

    }
}
