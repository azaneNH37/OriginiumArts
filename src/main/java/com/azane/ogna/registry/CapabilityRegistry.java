package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistry
{
    public static final Capability<IOgnaWeaponCap> OGNA_WEAPON = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IOgnaWeaponCap.class);
    }
}