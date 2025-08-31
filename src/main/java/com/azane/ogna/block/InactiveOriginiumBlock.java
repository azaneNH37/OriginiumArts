package com.azane.ogna.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;

import static com.azane.ogna.util.BlockStateUtil.IOGNM_LEVEL;

/**
 * @author azaneNH37 (2025-08-03)
 */
public class InactiveOriginiumBlock extends Block
{
    //private static final IntegerProperty IOGNM_LEVEL = IntegerProperty.create("level",0, 2);

    public final BlockState SHELL;
    public final BlockState MIX1;
    public final BlockState MIX2;
    public final BlockState MIX3;
    public final BlockState MIX4;
    public final BlockState CORE;

    public InactiveOriginiumBlock()
    {
        super(Properties.of().mapColor(MapColor.COLOR_BLACK)
            .strength(4F,3F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .lightLevel(state -> state.getValue(IOGNM_LEVEL) * 3));
        SHELL = this.getStateDefinition().any().setValue(IOGNM_LEVEL,0);
        MIX1 = this.getStateDefinition().any().setValue(IOGNM_LEVEL,1);
        MIX2 = this.getStateDefinition().any().setValue(IOGNM_LEVEL,2);
        MIX3 = this.getStateDefinition().any().setValue(IOGNM_LEVEL,3);
        MIX4 = this.getStateDefinition().any().setValue(IOGNM_LEVEL,4);
        CORE = this.getStateDefinition().any().setValue(IOGNM_LEVEL,5);
        this.registerDefaultState(SHELL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(IOGNM_LEVEL);
    }

    @Override
    public boolean skipRendering(BlockState pState, BlockState pAdjacentState, Direction pDirection)
    {
        return pAdjacentState.is(this) && pAdjacentState.getValue(IOGNM_LEVEL).equals(pState.getValue(IOGNM_LEVEL));
    }
}
