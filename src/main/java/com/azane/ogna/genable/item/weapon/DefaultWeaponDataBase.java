package com.azane.ogna.genable.item.weapon;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.AnimeDatumData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.combat.data.weapon.OgnaWeaponData;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.data.display.WeaponDisplayContext;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.lib.IComponentDisplay;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-11)
 */
public class DefaultWeaponDataBase implements IComponentDisplay
{
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("display_context")
    @Getter
    private WeaponDisplayContext displayContext = new WeaponDisplayContext();

    @SerializedName("anime_datum")
    private AnimeDatumData animeDatumData = new AnimeDatumData();

    @SerializedName("attack_entities")
    @Getter
    private AtkEntityData atkEntities;

    @SerializedName("weapon_data")
    @Getter
    private OgnaWeaponData ognaWeaponData;

    public Datums getAnimeDatum(ItemDisplayContext context, String animeName)
    {
        return animeDatumData.getAnimeDatumMap().getOrDefault(animeName,AnimeDatumData.DEFAULT_UNIT).getDatum(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.translatable(displayContext.getName()).withStyle(Style.EMPTY.withColor(displayContext.getColor())));
        tooltip.add(Component.translatable(displayContext.getDescription()).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("attribute.name.ognmarts.weapon.attack.cd").append(" %.1fs".formatted(ognaWeaponData.getCooldownTime()/1000f)).withStyle(ChatFormatting.YELLOW)
                .append("  ")
                .append(Component.translatable("attribute.name.ognmarts.weapon.reload.cd").append(" %.1fs".formatted(ognaWeaponData.getReloadTime()/1000f)).withStyle(ChatFormatting.GREEN)));
        //tooltip.add(Component.translatable("attribute.name.ognmarts.weapon.charge.max").append(" " + ognaWeaponData.getMaxChargeTime()).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("attribute.name.ognmarts.weapon.energy.store").append(" %.0f".formatted(ognaWeaponData.getMaxEnergy())).withStyle(ChatFormatting.WHITE)
                .append("  ")
                .append(Component.translatable("attribute.name.ognmarts.weapon.energy.consume").append(" %.0f".formatted(ognaWeaponData.getConsumption())).withStyle(ChatFormatting.AQUA)));
        tooltip.add(Component.translatable("attribute.name.ognmarts.chip.set.volume").append(" %.0f".formatted(ognaWeaponData.getChipSetVolume())).withStyle(ChatFormatting.GOLD));
        if(!ognaWeaponData.getInnerChips().isEmpty())
            tooltip.add(Component.empty());
        ognaWeaponData.getInnerChips().forEach(chipId -> {
            if (OgnaChip.getChip(chipId) != null)
                OgnaChip.getChip(chipId).appendHoverText(stack,tooltip,flag);
        });
        tooltip.add(Component.empty());
        if(!ognaWeaponData.getAttrModifiers().isEmpty())
            tooltip.add(Component.translatable("ogna.tip.skill.base.attr").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        ognaWeaponData.getAttrModifiers().forEach(attrModifier -> attrModifier.appendHoverText(stack,tooltip,flag));
    }
}