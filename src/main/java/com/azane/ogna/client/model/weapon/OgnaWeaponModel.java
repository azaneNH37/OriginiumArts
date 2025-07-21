package com.azane.ogna.client.model.weapon;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.weapon.OgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class OgnaWeaponModel<T extends OgnaWeapon> extends DefaultedItemGeoModel<T>
{
    @Nullable
    protected ItemStack currentItemStack;
    @Nullable
    protected ItemDisplayContext renderPerspective;

    public OgnaWeaponModel()
    {
        super(RlHelper.build(OriginiumArts.MOD_ID,"staff"));
    }

    public void updateCurrentRender(@Nullable ItemStack itemStack,@Nullable ItemDisplayContext renderPerspective)
    {
        this.currentItemStack = itemStack;
        this.renderPerspective = renderPerspective;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable)
    {
        return buildFormattedAnimationPath(animatable.getAnimation(currentItemStack));
    }

    @Override
    public ResourceLocation getModelResource(T animatable)
    {
        return buildFormattedModelPath(animatable.getModel(currentItemStack));
    }

    @Override
    public ResourceLocation getTextureResource(T animatable)
    {
        return buildFormattedTexturePath(animatable.getTexture(currentItemStack));
    }
}