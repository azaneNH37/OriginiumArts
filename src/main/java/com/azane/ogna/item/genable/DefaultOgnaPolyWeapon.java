package com.azane.ogna.item.genable;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.capability.weapon.OgnaWeaponCapProvider;
import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.item.weapon.IDefaultOgnaWeaponDataBase;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.CapabilityRegistry;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.Optional;

public abstract class DefaultOgnaPolyWeapon extends OgnaWeapon
{
    public static final String DEFAULT_CONTROLLER = "default";
    
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public DefaultOgnaPolyWeapon()
    {
        super(new Properties().stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public abstract IDefaultOgnaWeaponDataBase getDefaultDatabase(ItemStack stack);

    public abstract Map<Integer,String> getAnimeHashMap();

    @Override
    public ResourceLocation getModel(ItemStack stack)
    {
        IDefaultOgnaWeaponDataBase dataBase = getDefaultDatabase(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getModel).orElse(dataBase.getId());
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack)
    {
        IDefaultOgnaWeaponDataBase dataBase = getDefaultDatabase(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getTexture).orElse(dataBase.getId());
    }

    @Override
    public ResourceLocation getAnimation(ItemStack stack)
    {
        IDefaultOgnaWeaponDataBase dataBase = getDefaultDatabase(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getAnimation).orElse(dataBase.getId());
    }

    @Override
    public ResourceLocation getGuiModel(ItemStack stack)
    {
        IDefaultOgnaWeaponDataBase dataBase = getDefaultDatabase(stack);
        ResourceLocation id = dataBase.getId();
        return RlHelper.build(id.getNamespace(),"item_gui/"+id.getPath()+".gui");
    }

    @Override
    public Datums getCurrentAnimeDatums(ItemStack stack, ItemDisplayContext context, int animeHash)
    {
        return getDefaultDatabase(stack).getAnimeDatum(context,getAnimeHashMap().getOrDefault(animeHash,"unknown"));
    }

    @Override
    public String getControllerName(ItemStack stack){ return DEFAULT_CONTROLLER; }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new OgnaWeaponCapProvider(getDefaultDatabase(stack).getOgnaWeaponData(),nbt);
    }

    @Override
    public IOgnaWeaponCap getWeaponCap(ItemStack stack)
    {
        return stack.getCapability(CapabilityRegistry.OGNA_WEAPON).resolve().orElseGet(
            ()->{
                DebugLogger.error("capability should not be missing for stack: "+stack);
                return IOgnaWeaponCap.FALLBACK;
            }
        );
    }



    @Override
    public void onServerAttack(ItemStack stack, ServerPlayer player, AttackType attackType, long chargeTime)
    {
        DebugLogger.log("Server attack");
    }

    @Override
    public void onServerReload(ItemStack stack, ServerPlayer player)
    {
        DebugLogger.log("Server reload");
    }
}