package com.azane.ogna.client.renderer;

/**
 * 用于在非绑定手部的{@link net.minecraft.world.item.Item}绘制中将绘制基准点变换到合理位置<br>
 * 很明显现在的translate是基于玩家体型所做的硬编码变换 很可能会遇到渲染问题当itemStack所属实体的模型与玩家模型差距过大<br>
 * 目前使用的标准朝向为Blockbench中geckolib插件中模型N向提示对应实际游戏中玩家前方，基准点使用(0,0,0)点
 * TODO:计划引入与碰撞箱相关的动态绘制偏移 和基于标准基准点的自定义修正
 */
public enum Datums
{
    NONE(0,0,0),
    FIRST_PLAYER_HAND(0D,-1D,-1D),
    THIRD_PLAYER_RIGHT_HAND(0D,-1D,-1D),
    THIRD_PLAYER_CENTRAL(-0.5D,-1D,-0.5D),
    THIRD_PLAYER_FRONT(-0.5D,-1D,-1.5D);

    final double dx;
    final double dy;
    final double dz;

    Datums(double dx,double dy,double dz)
    {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
}