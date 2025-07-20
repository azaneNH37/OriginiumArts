package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.DmgBucket;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * OGNA持久化属性修改器类，用于存储属性的修改信息
 */
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class AttrModifier
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
}
