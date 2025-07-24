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
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ItemRegistry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
@JsonClassTypeBinder(fullName = "skill.default",simpleName = "sk.d",namespace = OriginiumArts.MOD_ID)
public class DefaultSkillDataBase implements ISkill
{
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;
    @SerializedName("icon")
    private ResourceLocation icon;
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
        Item item = ItemRegistry.OGNA_SKILL.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(ISkill.class).registerDataBase(this);
        }
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ItemRegistry.OGNA_SKILL.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }
}
