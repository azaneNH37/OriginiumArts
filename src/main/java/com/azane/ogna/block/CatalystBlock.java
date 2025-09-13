package com.azane.ogna.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class CatalystBlock extends Block
{
    public enum Type {RED,BLUE, GREEN,WHITE}

    public final Type type;

    public CatalystBlock(Type type)
    {
        super(BlockBehaviour.Properties.of().strength(1));
        this.type = type;
    }
}
