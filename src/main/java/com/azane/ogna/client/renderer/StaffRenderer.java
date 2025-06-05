package com.azane.ogna.client.renderer;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.model.StaffModel;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.item.StaffItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class StaffRenderer extends GeoItemRenderer<StaffItem>
{
    public StaffRenderer()
    {
        super(new StaffModel());
    }

    public static Consumer<PoseStack> datumBasisTransform = poseStack -> {
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180F));
    };

    private boolean needFreeTransform(ItemDisplayContext transformType)
    {
        return transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    private void applyFreeItemTransform(ItemStack stack,ItemDisplayContext transformType,PoseStack poseStack)
    {
        //discard all the hand-based transformation
        //maybe encounter issues if someone adds a custom transformation
        //DebugLogger.log("poseStack layers: \n"+ poseStack.last().pose() + poseStack.last().normal());
        if(transformType.firstPerson())
        {
            popDownPoseStack(poseStack,1);
            poseStack.translate(0F,-1F,-1F);//in hand
        } else{
            popDownPoseStack(poseStack,3);
            //default third person transformation in order to move the model to right-hand side
            //poseStack.mulPose(Axis.ZP.rotationDegrees(-180F));

            //for YSM we should disable the mulPose above, then apply somekinds of translate
            datumBasisTransform.accept(poseStack);

            //same reason, this.animatable is set in super.renderByItem
            this.animatable = (StaffItem) stack.getItem();
            int curAniHash = getCurrentAnimationHash(this.animatable, stack, "default").orElse(0);
            Datums datums = this.animatable.gainAnimeDatums(curAniHash);
            poseStack.translate(datums.dx,datums.dy,datums.dz);
        }
    }

    public static void popDownPoseStack(PoseStack poseStack,int maintain)
    {
        if(maintain < 1)
            return;
        Deque<PoseStack.Pose> poses = new LinkedList<>();
        while (!poseStack.clear())
        {
            poses.addFirst(poseStack.last());
            poseStack.popPose();
        }
        for(int i = 0; i < maintain-1 && !poses.isEmpty(); i++)
        {
            poseStack.pushPose();
            PoseStack.Pose pose = poses.removeFirst();
            poseStack.last().pose().set(pose.pose());
            poseStack.last().normal().set(pose.normal());
        }
        for(int i = 0;i < poses.size();i++)
            poseStack.pushPose();
    }

    public static <T extends Item & GeoAnimatable> Optional<Integer> getCurrentAnimationHash(T animatable,ItemStack stack, String controllerName)
    {
        //before super.renderByItem, currentItemStack isn't set yet,so we can't use this.getInstanceId here
        long uniqueId = GeoItem.getId(stack);
        AnimatableManager<?> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(uniqueId);
        //if two of the raw animations' hashCodes happens to be the same, well, why not go for a lottery?
        return Optional.ofNullable(animatableManager.getAnimationControllers().get(controllerName)).map(AnimationController::getCurrentRawAnimation).map(RawAnimation::hashCode);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        //applyFreeItemTransform(stack, transformType, poseStack);
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, StaffItem animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        if(!isReRender && needFreeTransform(renderPerspective))
        {
            applyFreeItemTransform(currentItemStack, renderPerspective, poseStack);
            preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
