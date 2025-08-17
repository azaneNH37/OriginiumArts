package com.azane.ogna.item.weapon;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.capability.weapon.OgnaWeaponCapProvider;
import com.azane.ogna.client.gameplay.ReloadState;
import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.combat.util.EnergyConsumer;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.item.weapon.IDefaultOgnaWeaponDataBase;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.SyncReloadStatePacket;
import com.azane.ogna.registry.ModCapability;
import com.azane.ogna.registry.ModAttribute;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author azaneNH37 (2025-08-11)
 */
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
        return new OgnaWeaponCapProvider(stack,nbt);
    }

    @Override
    public IOgnaWeaponCap getWeaponCap(ItemStack stack)
    {
        return stack.getCapability(ModCapability.OGNA_WEAPON).resolve().orElseGet(
            ()->{
                DebugLogger.error("capability should not be missing for stack: "+stack);
                return IOgnaWeaponCap.FALLBACK;
            }
        );
    }

    @Override
    public String getDescriptionId(ItemStack pStack)
    {
        return getDefaultDatabase(pStack).getDisplayContext().getName();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.clear();
        IDefaultOgnaWeaponDataBase weaponDataBase = getDefaultDatabase(pStack);
        weaponDataBase.appendHoverText(pStack, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.empty());
        getWeaponCap(pStack).appendHoverText(pStack, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void onServerAttack(ItemStack stack, ServerPlayer player, AttackType attackType, long chargeTime)
    {
        //DebugLogger.log("Server attack");
    }

    @Override
    public void onServerReload(ItemStack stack, ServerPlayer player)
    {
        //DebugLogger.log("Server reload");
        IOgnaWeaponCap cap = getWeaponCap(stack);
        double maxEnergy = cap.submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_STORE.get(),player,stack);
        int supposeGain = Mth.ceil(maxEnergy-cap.getCurrentEnergy());
        double gain = EnergyConsumer.convertItems(player,supposeGain);
        cap.modifyCurrentEnergy(Mth.clamp(gain,0,supposeGain),
            true, player, stack);
        if(gain<=supposeGain-1)
            OgnmChannel.DEFAULT.sendTo(new SyncReloadStatePacket(ReloadState.OUT_OF_ENERGY),player);
        else
            OgnmChannel.DEFAULT.sendTo(new SyncReloadStatePacket(ReloadState.COMPLETE),player);
    }
}