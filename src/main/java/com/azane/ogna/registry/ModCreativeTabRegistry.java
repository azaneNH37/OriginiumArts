package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.genable.OgnaStaff;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabRegistry
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OriginiumArts.MOD_ID);
    public static RegistryObject<CreativeModeTab> CJSL_TEST = TABS.register("staff", () -> CreativeModeTab.builder()
        .title(Component.translatable("ogna.tab.azane"))
        .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
        .displayItems((parameters, output) -> output.acceptAll(OgnaStaff.fillCreativeTab())).build());
}
