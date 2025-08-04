package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.ArkMaterial;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.OgnaStaff;
import com.azane.ogna.item.weapon.OgnaSword;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItem
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OriginiumArts.MOD_ID);

    public static final RegistryObject<Item> OGNA_STAFF = register("ogna_staff",false, OgnaStaff::new);
    public static final RegistryObject<Item> OGNA_SWORD = register("ogna_sword",false, OgnaSword::new);

    public static final RegistryObject<Item> OGNA_SKILL = register("ogna_skill",false, OgnaSkill::new);
    public static final RegistryObject<Item> OGNA_CHIP = register("ogna_chip", false,OgnaChip::new);

    public static final RegistryObject<Item> OGNM_SHARD = register("ognm_shard",()-> new ArkMaterial(5));
    public static final RegistryObject<Item> ORIROCK_CUBE = register("orirock_cube",()-> new ArkMaterial(2));
    public static final RegistryObject<Item> ORIRON = register("oriron",()-> new ArkMaterial(2));

    public static RegistryObject<Item> register(String name, boolean isInCreativeTab,Supplier<? extends Item> supplier)
    {
        RegistryObject<Item> item = ITEMS.register(name,supplier);
        if (isInCreativeTab)
            ModCreativeTab.CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    public static RegistryObject<Item> register(String name, Supplier<? extends Item> supplier)
    {
        return register(name,true,supplier);
    }
}
