package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.ArkDmgTypes;
import com.azane.ogna.genable.item.chip.ItemChip;
import com.azane.ogna.registry.ModAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author azaneNH37 (2025/8/15)
 */
@JsonClassTypeBinder(fullName = "chip.tle.boke", namespace = OriginiumArts.MOD_ID)
public class TLEThresherChip extends ItemChip
{
    @Override
    public List<ChipTiming> registerTiming() {return List.of(ChipTiming.ON_HIT_ENTITY);}

    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity target, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
        double armor = target.getAttributeValue(Attributes.ARMOR);
        double armorToughness = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        target.hurt(new DamageSource(ArkDmgTypes.getHolder(ArkDmgTypes.DOT, target.level().isClientSide())), (float) combatUnit.getMatrices().get(ModAttribute.DAMAGE_ARTS.get()).submit(calculateArmorPenaltyDamage(armor,armorToughness)));
    }

    public double calculateArmorPenaltyDamage(double armor, double armorToughness)
    {
        if (armor < 0) armor = 0;
        if (armorToughness < 0.0f) armorToughness = 0.0f;

        final float BASE_PENALTY = 2f;
        final float armorPenaltyFactor = 0.075f;
        final float toughnessPenaltyFactor = 0.75f;

        return (BASE_PENALTY + (Math.pow(armor,1.7f) * armorPenaltyFactor) + (armorToughness * toughnessPenaltyFactor));
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, tooltip, flag);
        tooltip.add(Component.translatable("ogna.genable.chip.tle.thresher.content"));
    }
}
