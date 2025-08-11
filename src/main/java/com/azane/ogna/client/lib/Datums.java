package com.azane.ogna.client.lib;

/**
 * 用于在非绑定手部的{@link net.minecraft.world.item.Item}绘制中将绘制基准点变换到合理位置<br>
 * 很明显现在的translate是基于玩家体型所做的硬编码变换 很可能会遇到渲染问题当itemStack所属实体的模型与玩家模型差距过大<br>
 * 目前使用的标准朝向为Blockbench中geckolib插件中模型N向提示对应实际游戏中玩家前方，基准点使用(0,0,0)点
 * TODO:计划引入与碰撞箱相关的动态绘制偏移 和基于标准基准点的自定义修正
 * @author azaneNH37 (2025-08-04)
 */
public enum Datums
{
    none(0,0,0),
    hand1(0D,-0.8D,-0.8D),
    central1(-0.5D,-0.5D,-1D),
    front1(-0.5D,-0.5D,-1.25D),
    hand3(-0.1D,-0.8D,-0.8D),
    central3(-0.5D,-0.5D,-0.5D),
    front3(-0.5D,-0.5D,-1.5D);

    public final double dx;
    public final double dy;
    public final double dz;

    Datums(double dx,double dy,double dz)
    {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
}