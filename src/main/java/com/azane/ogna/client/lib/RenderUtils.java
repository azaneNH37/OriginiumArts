package com.azane.ogna.client.lib;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author azaneNH37 (2025/8/31)
 */
@OnlyIn(Dist.CLIENT)
public class RenderUtils {

    /**
     * 在指定位置绘制一个矩形纹理背景
     * @param poseStack 当前的PoseStack
     * @param bufferSource 缓冲区源
     * @param texture 纹理资源位置
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @param zOffset Z轴偏移（用于控制前后顺序）
     * @param color RGBA颜色（0-255）
     * @param packedLight 光照值
     * @param packedOverlay 覆盖值
     */
    public static void renderRectTexture(PoseStack poseStack, MultiBufferSource bufferSource,
                                         ResourceLocation texture, float x, float y,
                                         float width, float height, float zOffset,
                                         int[] color, int packedLight, int packedOverlay) {
        // 使用适合的渲染类型
        RenderType renderType = RenderType.entityTranslucent(texture);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        // 提取颜色分量
        int r = color[0];
        int g = color[1];
        int b = color[2];
        int a = color.length > 3 ? color[3] : 255;

        // 渲染矩形的正面
        vertexConsumer.vertex(matrix, x, y, zOffset)
            .color(r, g, b, a)
            .uv(0.0f, 0.0f)
            .overlayCoords(packedOverlay)
            .uv2(packedLight)
            .normal(normalMatrix, 0.0F, 0.0F, 1.0F)
            .endVertex();

        vertexConsumer.vertex(matrix, x + width, y, zOffset)
            .color(r, g, b, a)
            .uv(1.0f, 0.0f)
            .overlayCoords(packedOverlay)
            .uv2(packedLight)
            .normal(normalMatrix, 0.0F, 0.0F, 1.0F)
            .endVertex();

        vertexConsumer.vertex(matrix, x + width, y + height, zOffset)
            .color(r, g, b, a)
            .uv(1.0f, 1.0f)
            .overlayCoords(packedOverlay)
            .uv2(packedLight)
            .normal(normalMatrix, 0.0F, 0.0F, 1.0F)
            .endVertex();

        vertexConsumer.vertex(matrix, x, y + height, zOffset)
            .color(r, g, b, a)
            .uv(0.0f, 1.0f)
            .overlayCoords(packedOverlay)
            .uv2(packedLight)
            .normal(normalMatrix, 0.0F, 0.0F, 1.0F)
            .endVertex();

        poseStack.popPose();
    }

    /**
     * 重载方法，使用默认白色和全透明度
     */
    public static void renderRectTexture(PoseStack poseStack, MultiBufferSource bufferSource,
                                         ResourceLocation texture, float x, float y,
                                         float width, float height, float zOffset,
                                         int packedLight, int packedOverlay) {
        renderRectTexture(poseStack, bufferSource, texture, x, y, width, height, zOffset,
            new int[]{255, 255, 255, 255}, packedLight, packedOverlay);
    }

    /**
     * 重载方法，使用默认位置（居中）和默认颜色
     */
    public static void renderRectTexture(PoseStack poseStack, MultiBufferSource bufferSource,
                                         ResourceLocation texture, float width, float height,
                                         float zOffset, int packedLight, int packedOverlay) {
        renderRectTexture(poseStack, bufferSource, texture, -width/2, -height/2,
            width, height, zOffset, packedLight, packedOverlay);
    }

    // 辅助类用于存储矩阵对
    private record MatrixPair(Matrix4f pose, Matrix3f normal) { }

    public static PoseStack copyPoseStack(PoseStack original) {
        if (original == null) {
            return null;
        }

        // 存储原始栈所有层的矩阵副本（从栈底到栈顶）
        List<MatrixPair> matrices = new ArrayList<>();

        // 临时弹出原始栈的 pose 并记录矩阵副本
        while (!original.clear()) {
            PoseStack.Pose topPose = original.last();
            // 创建矩阵的深拷贝
            Matrix4f poseMatrix = new Matrix4f(topPose.pose());
            Matrix3f normalMatrix = new Matrix3f(topPose.normal());
            // 将拷贝添加到列表开头（以保持栈底到栈顶的顺序）
            matrices.add(0, new MatrixPair(poseMatrix, normalMatrix));
            original.popPose();
        }

        // 记录最后一个（初始）pose 的矩阵
        PoseStack.Pose bottomPose = original.last();
        matrices.add(0, new MatrixPair(new Matrix4f(bottomPose.pose()), new Matrix3f(bottomPose.normal())));

        // 还原原始栈：首先设置初始 pose 的矩阵
        original.last().pose().set(matrices.get(0).pose);
        original.last().normal().set(matrices.get(0).normal);

        // 然后 pushPose 并设置后续矩阵
        for (int i = 1; i < matrices.size(); i++) {
            original.pushPose();
            original.last().pose().set(matrices.get(i).pose);
            original.last().normal().set(matrices.get(i).normal);
        }

        // 创建新的 PoseStack 副本
        PoseStack copy = new PoseStack();
        // 设置初始 pose 的矩阵
        copy.last().pose().set(matrices.get(0).pose);
        copy.last().normal().set(matrices.get(0).normal);

        // pushPose 并设置后续矩阵
        for (int i = 1; i < matrices.size(); i++) {
            copy.pushPose();
            copy.last().pose().set(matrices.get(i).pose);
            copy.last().normal().set(matrices.get(i).normal);
        }

        return copy;
    }
}
