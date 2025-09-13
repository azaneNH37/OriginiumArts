package com.azane.ogna.craft.catalyst;

import com.azane.ogna.block.CatalystBlock;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import java.util.*;

/**
 * 催化剂需求类，用于定义配方所需的催化剂条件
 * @author azaneNH37 (2025-09-12)
 */
@Getter
public class CatalystRequirement
{
    private final Map<CatalystBlock.Type, Integer> requirements;
    /**
     *  获取所需催化剂的总数量，用于配方优先级排序
     */
    private final int totalRequiredCount;

    public CatalystRequirement() {
        this.requirements = new EnumMap<>(CatalystBlock.Type.class);
        this.totalRequiredCount = 0;
    }

    public CatalystRequirement(Map<CatalystBlock.Type, Integer> requirements) {
        this.requirements = new EnumMap<>(requirements);
        this.totalRequiredCount = requirements.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 检查给定的催化剂数量是否满足需求
     */
    public boolean isSatisfiedBy(Map<CatalystBlock.Type, Integer> available) {
        for (Map.Entry<CatalystBlock.Type, Integer> entry : requirements.entrySet()) {
            int required = entry.getValue();
            int availableCount = available.getOrDefault(entry.getKey(), 0);
            if (availableCount < required) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否需要催化剂
     */
    public boolean isEmpty() {return requirements.isEmpty();}

    /**
     * 从JSON解析催化剂需求
     */
    public static CatalystRequirement fromJson(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return new CatalystRequirement();
        }

        Map<CatalystBlock.Type, Integer> requirements = new EnumMap<>(CatalystBlock.Type.class);

        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    String typeStr = GsonHelper.getAsString(obj, "type");
                    int count = GsonHelper.getAsInt(obj, "count", 1);

                    try {
                        CatalystBlock.Type type = CatalystBlock.Type.valueOf(typeStr.toUpperCase());
                        requirements.put(type, requirements.getOrDefault(type, 0) + count);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        return new CatalystRequirement(requirements);
    }

    /**
     * 写入到网络缓冲区
     */
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeVarInt(requirements.size());
        for (Map.Entry<CatalystBlock.Type, Integer> entry : requirements.entrySet()) {
            buffer.writeEnum(entry.getKey());
            buffer.writeVarInt(entry.getValue());
        }
    }

    /**
     * 从网络缓冲区读取
     */
    public static CatalystRequirement fromNetwork(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        Map<CatalystBlock.Type, Integer> requirements = new EnumMap<>(CatalystBlock.Type.class);

        for (int i = 0; i < size; i++) {
            CatalystBlock.Type type = buffer.readEnum(CatalystBlock.Type.class);
            int count = buffer.readVarInt();
            requirements.put(type, count);
        }

        return new CatalystRequirement(requirements);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CatalystRequirement other)) return false;
        return requirements.equals(other.requirements);
    }

    @Override
    public int hashCode() {
        return requirements.hashCode();
    }

    @Override
    public String toString() {
        return "CatalystRequirement{" + requirements + "}";
    }
}