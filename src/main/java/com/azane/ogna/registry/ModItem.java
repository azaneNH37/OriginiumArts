package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.OgnaStaff;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItem
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OriginiumArts.MOD_ID);

    public static final RegistryObject<Item> OGNA_STAFF = register("ogna_staff", OgnaStaff::new);
    public static final RegistryObject<Item> OGNA_SKILL = register("ogna_skill", OgnaSkill::new);
    public static final RegistryObject<Item> OGNA_CHIP = register("ogna_chip", OgnaChip::new);

    public static RegistryObject<Item> register(String name, Supplier<? extends Item> supplier)
    {
        return ITEMS.register(name,supplier);
    }
}
