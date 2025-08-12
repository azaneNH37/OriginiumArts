package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

//TODO: 资源包直读支持
/**
 * 临时的解决方案，未引入动态支持
 * @author azaneNH37 (2025/8/12)
 */
public class ModSound
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OriginiumArts.MOD_ID);

    public static final Map<String,RegistryObject<SoundEvent>> SOUND_MAP = new HashMap<>();

    public static final SoundKeyData.SoundKeyUnit SKILL_START_UNIT = new SoundKeyData.SoundKeyUnit("skill.start", 1.0F, 1.0F);

    static {
        register(
            //Skill Common
            "skill.start",
            //Weapon
            "attack.time",
            "attack.fire",
            "attack.poison",
            "attack.blade.fire",
            "hit.range",
            "hit.fire",
            //Skill
            "skill.key.attack",
            "skill.twilight.hit",
            "skill.volcano.loop",
            "skill.destreza.attack"
        );
    }

    public static void register(String... ids)
    {
        for(var id : ids)
        {
            SOUND_MAP.put(id,SOUND_EVENTS.register(id,()->SoundEvent.createVariableRangeEvent(RlHelper.build(OriginiumArts.MOD_ID,id))));
        }
    }
}
