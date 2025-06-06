package com.azane.ogna.genable.item;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.WeaponAtkEntityData;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.registry.ItemRegistry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@JsonClassTypeBinder(fullName = "staff", simpleName = "staff", namespace = OriginiumArts.MOD_ID)
public class StaffDataBase implements IStaffDataBase
{
    //TODO: a fail-safe id
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("anime_datum_first")
    private Map<String,Datums> animeDatumMapFirst;
    @SerializedName("anime_datum_third")
    private Map<String,Datums> animeDatumMapThird;

    @SerializedName("attack_entities")
    @Getter
    private WeaponAtkEntityData atkEntities;


    @Override
    public void registerDataBase()
    {
        Item item = ItemRegistry.OGNA_STAFF.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(IStaffDataBase.class).registerDataBase(this);
        }
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ItemRegistry.OGNA_STAFF.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }

    @Override
    public Datums getAnimeDatum(ItemDisplayContext context, String animeName)
    {
        return context.firstPerson() ?
            animeDatumMapFirst.getOrDefault(animeName, Datums.FIRST_PLAYER_HAND) :
            animeDatumMapThird.getOrDefault(animeName, Datums.NONE);
    }
}
