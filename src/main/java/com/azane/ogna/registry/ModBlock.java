package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.*;
import com.azane.ogna.item.geoblock.CraftOCCBlockItem;
import com.azane.ogna.item.geoblock.InjectEPTBlockItem;
import com.lowdragmc.lowdraglib.test.TestJava;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlock
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OriginiumArts.MOD_ID);

    public static final ItemBlock CRAFT_OCC = registerBlockItem("craft_occ", CraftOCCBlock::new, block -> new CraftOCCBlockItem(block, new Item.Properties()));
    public static final ItemBlock INJECT_EPT = registerBlockItem("inject_ept", InjectEPTBlock::new,block-> new InjectEPTBlockItem(block,new Item.Properties()));
    public static final ItemBlock ENERGY_EH = registerBlockItem("energy_eh", EnergyEHBlock::new);

    public static final ItemBlock AOGNM_L = registerBlockItem("aognm_l", ()->new ActiveOriginiumBlock(31,0.2D));
    public static final ItemBlock AOGNM_M = registerBlockItem("aognm_m", ()->new ActiveOriginiumBlock(15,0.12D));
    public static final ItemBlock AOGNM_S = registerBlockItem("aognm_s", ()->new ActiveOriginiumBlock(7,0.05D));
    public static final ItemBlock IOGNM = registerBlockItem("iognm", InactiveOriginiumBlock::new);

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
    public static ItemBlock registerBlockItem(String name, Supplier<? extends Block> supplier, Function<Block,BlockItem> itemSupplier)
    {
        return new ItemBlock(name, supplier, itemSupplier);
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

        public ItemBlock(String name, Supplier<? extends Block> supplier, Function<Block,BlockItem> itemSupplier)
        {
            this.block = register(name,supplier);
            this.item = ModItem.register(name, () -> itemSupplier.apply(block.get()));
        }
    }

}
