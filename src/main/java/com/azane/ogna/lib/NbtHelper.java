package com.azane.ogna.lib;

import net.minecraft.nbt.Tag;

import net.minecraft.nbt.*;
import java.util.Map;
import java.util.function.BiFunction;

public class NbtHelper
{

    @FunctionalInterface
    public interface NbtWriter<T> {
        void write(CompoundTag tag, String key, T value);
    }

    // --- 写操作的函数映射 ---
    // 映射：Java类型 -> (CompoundTag, 键, 值) -> void
    // 使用Lambda表达式手动处理拆箱
    private static final Map<Class<?>, NbtWriter<?>> WRITERS = Map.of(
        String.class, (tag, key, value) -> tag.putString(key, (String) value),
        Integer.class, (tag, key, value) -> tag.putInt(key, (Integer) value),
        Boolean.class, (tag, key, value) -> tag.putBoolean(key, (Boolean) value),
        Double.class, (tag, key, value) -> tag.putDouble(key, (Double) value),
        Float.class, (tag, key, value) -> tag.putFloat(key, (Float) value),
        Byte.class, (tag, key, value) -> tag.putByte(key, (Byte) value),
        Long.class, (tag, key, value) -> tag.putLong(key, (Long) value),
        Short.class, (tag, key, value) -> tag.putShort(key, (Short) value)
    );

    // --- 读操作的函数映射 ---
    // 映射：Java类型 -> (CompoundTag, 键) -> Java对象
    // BiFunction 的返回类型是 Object，我们通过类型转换来处理
    private static final Map<Class<?>, BiFunction<CompoundTag, String, Object>> READERS = Map.of(
        String.class, CompoundTag::getString,
        Integer.class, CompoundTag::getInt,
        Boolean.class, CompoundTag::getBoolean,
        Double.class, CompoundTag::getDouble,
        Float.class, CompoundTag::getFloat,
        Byte.class, CompoundTag::getByte,
        Long.class, CompoundTag::getLong,
        Short.class, CompoundTag::getShort
    );

    // --- 核心辅助方法 ---

    /**
     * 将一个值写入 CompoundTag。
     *
     * @param <T>   Java对象类型
     * @param tag   要写入的 CompoundTag
     * @param key   键
     * @param value 值
     */
    @SuppressWarnings("unchecked")
    public static <T> void put(CompoundTag tag, String key, T value) {
        // 根据值的类型获取对应的写入器
        try {
            NbtWriter<T> writer = (NbtWriter<T>) WRITERS.get(value.getClass());
            if (writer != null) {
                writer.write(tag, key, value);
            }
        }catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Unsupported type for NBT write: " + value.getClass().getName(), e);
        }
    }

    /**
     * 从 CompoundTag 中读取一个值。
     *
     * @param <T>   Java对象类型
     * @param tag   要读取的 CompoundTag
     * @param key   键
     * @param type  值类型
     * @return 读取到的值，如果键不存在或类型不匹配则返回 null
     */
    public static <T> T get(CompoundTag tag, String key, Class<T> type) {
        // 根据类型获取对应的读取器
        BiFunction<CompoundTag, String, Object> reader = READERS.get(type);
        if (reader != null && tag.contains(key, getTypeForClass(type))) {
            return type.cast(reader.apply(tag, key));
        }
        return null;
    }

    /**
     * 从 CompoundTag 中读取一个值，如果不存在，则写入默认值并返回。
     *
     * @param <T>           Java对象类型
     * @param tag           要读取的 CompoundTag
     * @param key           键
     * @param defaultValue  默认值
     * @return 读取到的值或默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrCreate(CompoundTag tag, String key, T defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("Default value cannot be null.");
        }
        Class<T> type = (Class<T>) defaultValue.getClass();

        // 检查 NBT 中是否存在对应类型的值
        if (tag.contains(key, getTypeForClass(type))) {
            return get(tag, key, type);
        } else {
            put(tag, key, defaultValue);
            return defaultValue;
        }
    }

    // 辅助方法：将 Java 类型映射到 NBT Tag 的 ID
    private static byte getTypeForClass(Class<?> type) {
        if (type == String.class) return Tag.TAG_STRING;
        if (type == Integer.class) return Tag.TAG_INT;
        if (type == Boolean.class || type == Byte.class) return Tag.TAG_BYTE;
        if (type == Double.class) return Tag.TAG_DOUBLE;
        if (type == Float.class) return Tag.TAG_FLOAT;
        if (type == Long.class) return Tag.TAG_LONG;
        if (type == Short.class) return Tag.TAG_SHORT;
        return Tag.TAG_END; // 未知类型
    }
}
