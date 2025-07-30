package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.entity.CraftOCCBlockEntity;
import com.azane.ogna.block.entity.InjectEPTBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntity
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OriginiumArts.MOD_ID);

    public static final RegistryObject<BlockEntityType<CraftOCCBlockEntity>> CRAFT_OCC_ENTITY =
        BLOCK_ENTITIES.register("craft_occ", () ->
            BlockEntityType.Builder.of(
                CraftOCCBlockEntity::new,
                ModBlock.CRAFT_OCC.block.get()
            ).build(DSL.remainderType())
        );

    public static final RegistryObject<BlockEntityType<InjectEPTBlockEntity>> INJECT_EPT_ENTITY =
        BLOCK_ENTITIES.register("inject_ept", () ->
            BlockEntityType.Builder.of(
                InjectEPTBlockEntity::new,
                ModBlock.INJECT_EPT.block.get()
            ).build(DSL.remainderType())
        );
}
