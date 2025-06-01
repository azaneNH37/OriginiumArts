package com.azane.ogna.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemGenRenderer extends BlockEntityWithoutLevelRenderer
{
    private final Minecraft minecraft;
    private ItemRenderer itemRenderer;

    public ItemGenRenderer(Minecraft minecraft)
    {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        this.minecraft = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay)
    {

    }
}
