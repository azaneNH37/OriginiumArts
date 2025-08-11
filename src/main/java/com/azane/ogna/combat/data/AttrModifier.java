package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.DmgBucket;
import com.azane.ogna.lib.IComponentDisplay;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

/**
 * OGNA持久化属性修改器类，用于存储属性的修改信息
 */
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class AttrModifier implements IComponentDisplay
{
    @SerializedName("attr")
    private ResourceLocation attribute;
    @SerializedName("id")
    private UUID id;
    @SerializedName("bucket")
    private DmgBucket bucket;
    @SerializedName("amount")
    private double amount;
    @SerializedName("stack_size")
    private int stackSize = 1;


    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        var attr = ForgeRegistries.ATTRIBUTES.getValue(attribute);
        var key = attr == null ? "<unknown attribute>" : attr.getDescriptionId();
        tooltip.add(
            Component.translatable(key).withStyle(ChatFormatting.BOLD,ChatFormatting.WHITE)
                .append(bucket.getFormat().apply(amount)));
    }
}
