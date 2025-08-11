package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.item.chip.NonItemChip;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxBlockEffectTriggerPacket;
import com.azane.ogna.network.to_client.FxEntityEffectTriggerPacket;
import com.azane.ogna.util.OgnaFxHelper;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-11)
 */
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonClassTypeBinder(fullName = "chip.inner.effect",namespace = OriginiumArts.MOD_ID)
public class InnerMobEffectChip extends NonItemChip
{
    @SerializedName("effectID")
    private ResourceLocation rl;
    @SerializedName("level")
    private int effect_level = -10;
    @SerializedName("duration")
    private int duration = -1;
    @SerializedName("visible")
    private boolean visible = false;
    @SerializedName("fx")
    private FxData fxData;

    @Override
    public boolean canPlugIn(ChipSet chipSet, ChipArg arg) {return true;}

    @Override
    public List<ChipTiming> registerTiming() {return List.of(ChipTiming.ON_HIT_ENTITY);}

    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity target, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(rl);
        if(effect == null)
        {
            DebugLogger.error("EfcEffectAdder: Effect %s not found, cannot apply to entity %s".formatted(rl, target.getStringUUID()));
            return;
        }
        if(fxData != null)
        {
            OgnaFxHelper.extractFxUnit(fxData,FxData::getHitFx)
                .map(FxData.FxUnit::getId).ifPresent(rl->{
                    OgnmChannel.DEFAULT.sendToWithinRange(
                        new FxEntityEffectTriggerPacket(rl,target.getId(),false),
                        level,
                        target.getOnPos(),
                        128
                    );
                });
        }
        target.addEffect(new MobEffectInstance(effect,duration,effect_level-1,false,visible,false));
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(rl);
        if(effect == null)
        {
            tooltip.add(Component.translatable("effect.unknown").withStyle(Style.EMPTY.withColor(0xFF0000)));
            return;
        }
        tooltip.add(Component.empty()
            .append(Component.translatable(displayContext.getName()).withStyle(Style.EMPTY.withColor(displayContext.getColor())).withStyle(ChatFormatting.BOLD))
            .append(" - ")
            .append(Component.translatable("ogna.tip.chip.inner.mobEffect",effect.getDisplayName().getString(),effect_level, duration < 0 ? "∞" : duration/20f)
            ));
    }
}
