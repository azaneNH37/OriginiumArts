package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.debug.GenSlashItem;
import com.azane.ogna.item.StaffItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OriginiumArts.MOD_ID);


    public static final RegistryObject<Item> D_GENSLASH = register("d_slash", GenSlashItem::new);
    public static final RegistryObject<Item> STAFF = register("staff", StaffItem::new);

    public static RegistryObject<Item> register(String name, Supplier<? extends Item> supplier)
    {
        return ITEMS.register(name,supplier);
    }
}
