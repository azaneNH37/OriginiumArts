package com.azane.ogna.client.lib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

public final class OffHandItemTransform
{
    public static final Consumer<PoseStack> RAW_BASIS = poseStack -> {
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180F));
    };
    public static final Consumer<PoseStack> YSM_BASIS = poseStack -> {
        poseStack.translate(0F,1.75F,0F);
    };

    public static Consumer<PoseStack> datumBasisTransform = RAW_BASIS;

    public static boolean needFreeTransform(ItemDisplayContext transformType)
    {
        return transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    public static void applyFreeItemTransform(PoseStack poseStack,ItemDisplayContext transformType,Datums curDatums)
    {
        if(!needFreeTransform(transformType))
            return;
        //discard all the hand-based transformation
        //maybe encounter issues if someone adds a custom transformation
        if(transformType.firstPerson())
        {
            popDownPoseStack(poseStack,1);
            //poseStack.translate(0F,-1F,-1F);//in hand
        } else{
            popDownPoseStack(poseStack,3);
            //default third person transformation in order to move the model to right-hand side
            //poseStack.mulPose(Axis.ZP.rotationDegrees(-180F));
            //for YSM we should disable the mulPose above, then apply somekinds of translate
            datumBasisTransform.accept(poseStack);
        }
        poseStack.translate(curDatums.dx,curDatums.dy,curDatums.dz);
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

    public static <T extends Item & GeoAnimatable> Optional<Integer> getCurrentAnimationHash(T animatable, ItemStack stack, String controllerName)
    {
        //before super.renderByItem, currentItemStack isn't set yet,so we can't use this.getInstanceId here
        long uniqueId = GeoItem.getId(stack);
        AnimatableManager<?> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(uniqueId);
        //if two of the raw animations' hashCodes happens to be the same, well, why not go for a lottery?
        return Optional.ofNullable(animatableManager.getAnimationControllers().get(controllerName)).map(AnimationController::getCurrentRawAnimation).map(RawAnimation::hashCode);
    }

    public static <T extends Item & GeoAnimatable & IOffHandItem> Datums getCurrentDatums(T animatable, ItemStack stack,ItemDisplayContext context)
    {
        return getCurrentAnimationHash(animatable, stack, animatable.getControllerName(stack))
            .map((hash) -> animatable.getCurrentAnimeDatums(stack, context,hash))
            .orElse(Datums.NONE);
    }
}
