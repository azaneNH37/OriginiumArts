package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.SlashEntity;
import com.azane.ogna.entity.genable.BladeEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OriginiumArts.MOD_ID);

    public static final RegistryObject<EntityType<SlashEntity>> SLASH = registerMisc("slash",SlashEntity::new,3.0F,1.0F);
    public static final RegistryObject<EntityType<BladeEffect>> BLADE_EFFECT = registerMisc("blade_effect",BladeEffect::new,1.0F,1.0F);

    public static <T extends Entity> RegistryObject<EntityType<T>> registerMisc(String name, EntityType.EntityFactory<T> entity, float width, float height)
    {
        return ENTITIES.register(name,
            () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(width, height).build(name));
    }
}
