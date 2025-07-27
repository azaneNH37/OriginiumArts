package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.CraftOCCBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlock
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OriginiumArts.MOD_ID);

    public static final ItemBlock CRAFT_OCC = registerBlockItem("craft_occ", CraftOCCBlock::new);

    public static RegistryObject<Block> register(String name, Supplier<? extends Block> supplier)
    {
        return BLOCKS.register(name,supplier);
    }

    public static ItemBlock registerBlockItem(String name, Supplier<? extends Block> supplier, Item.Properties properties)
    {
        return new ItemBlock(name,supplier,properties);
    }
    public static ItemBlock registerBlockItem(String name, Supplier<? extends Block> supplier)
    {
        return registerBlockItem(name,supplier,new Item.Properties());
    }

    public static class ItemBlock
    {
        public final RegistryObject<Block> block;
        public final RegistryObject<Item> item;

        public ItemBlock(String name, Supplier<? extends Block> supplier, Item.Properties properties)
        {
            this.block = register(name,supplier);
            this.item = ModItem.register(name,()-> new BlockItem(this.block.get(),properties));
        }
    }
}
