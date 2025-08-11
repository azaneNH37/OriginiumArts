package com.azane.ogna.client.renderer;

import com.azane.ogna.client.lib.IExtraModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-07-29)
 */
public class ExtraModelItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private final Minecraft minecraft;
    private ItemRenderer itemRenderer;
    private ModelManager modelManager;

    public ExtraModelItemRenderer(Minecraft minecraft)
    {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        this.minecraft = minecraft;
        itemRenderer = minecraft.getItemRenderer();
        modelManager = minecraft.getModelManager();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (stack.getItem() instanceof IExtraModel extraModelItem) {
            BakedModel bakedModel = modelManager.getModel(extraModelItem.getGuiModel(stack));
            //poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.popPose();
            poseStack.pushPose();
            boolean lefthand = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            itemRenderer.render(stack, displayContext, lefthand, poseStack, buffer, packedLight, packedOverlay, bakedModel);

        }
        else
        {
            super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
