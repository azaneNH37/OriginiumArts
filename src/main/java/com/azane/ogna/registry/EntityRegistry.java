package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.entity.genable.Bullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OriginiumArts.MOD_ID);

    public static final RegistryObject<EntityType<BladeEffect>> BLADE_EFFECT = ENTITIES.register("blade_effect",()->BladeEffect.TYPE);
    public static final RegistryObject<EntityType<Bullet>> BULLET = ENTITIES.register("bullet",()->Bullet.TYPE);

    public static <T extends Entity> RegistryObject<EntityType<T>> registerMisc(String name, EntityType.EntityFactory<T> entity, float width, float height)
    {
        return ENTITIES.register(name,
            () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(width, height).build(name));
    }
}
