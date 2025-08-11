package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.OgnaStaff;
import com.azane.ogna.item.weapon.OgnaSword;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author azaneNH37 (2025-08-04)
 */
public class ModCreativeTab
{
    public static final Set<RegistryObject<Item>> CREATIVE_TAB_ITEMS = new LinkedHashSet<>();

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OriginiumArts.MOD_ID);

    public static RegistryObject<CreativeModeTab> STAFF_TAB = TABS.register("staff", () -> CreativeModeTab.builder()
        .title(Component.translatable("ogna.tab.azane"))
        .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
        .displayItems((parameters, output) -> output.acceptAll(
            Stream.of(OgnaStaff.fillCreativeTab(), OgnaSword.fillCreativeTab(),OgnaSkill.fillCreativeTab(),OgnaChip.fillCreativeTab(),fillCreativeTab())
                .flatMap(List::stream).distinct().toList()
        )).build());

    public static NonNullList<ItemStack> fillCreativeTab()
    {
        NonNullList<ItemStack> items = NonNullList.create();
        CREATIVE_TAB_ITEMS.forEach(item -> items.add(new ItemStack(item.get())));
        return items;
    }
}
