package com.azane.ogna.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class InactiveOriginiumBlock extends Block
{
    public InactiveOriginiumBlock()
    {
        super(Properties.of().strength(3F,3F).sound(SoundType.METAL).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any());
    }
}
