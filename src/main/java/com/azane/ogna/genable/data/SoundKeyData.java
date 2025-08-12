package com.azane.ogna.genable.data;

import com.azane.ogna.registry.ModSound;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * @author azaneNH37 (2025/8/12)
 */
@Getter
public class SoundKeyData
{
    @SerializedName("awake")
    private SoundKeyUnit awakeSound;
    @SerializedName("hit")
    private SoundKeyUnit hitSound;

    public static Optional<SoundEvent> getSound(SoundKeyUnit key)
    {
        return Optional.ofNullable(key).map(SoundKeyUnit::getSoundKey).map(ModSound.SOUND_MAP::get).map(RegistryObject::get);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SoundKeyUnit
    {
        @SerializedName("key")
        private String soundKey;
        @SerializedName("volume")
        private float volume = 1.0F;
        @SerializedName("pitch")
        private float pitch = 1.0F;
    }
}