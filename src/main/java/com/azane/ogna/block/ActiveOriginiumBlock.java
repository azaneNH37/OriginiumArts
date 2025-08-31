package com.azane.ogna.block;

import com.azane.ogna.registry.ModBlock;
import com.azane.ogna.util.BlockStateUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;

import static com.azane.ogna.lib.BlockPosAxisHelper.*;
import static com.azane.ogna.util.BlockStateUtil.AOGNM_LEVEL;
import static com.azane.ogna.util.BlockStateUtil.AOGNM_ORI;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author azaneNH37 (2025-08-03)
 */
public class ActiveOriginiumBlock extends Block
{
    public static final int INERT = 1;
    public static final int ANGLE_MAX = 6;

    public final double CC_CHANCE;
    private final int SIZE;
    
    private static final IntegerProperty DIRECTION = IntegerProperty.create("direction",0,3);
    private static final IntegerProperty ANGLE = IntegerProperty.create("angle",0,ANGLE_MAX);
    private static final BooleanProperty GROW = BooleanProperty.create("grow");

    public ActiveOriginiumBlock(int size, double ccChance)
    {
        super(Properties.of().sound(SoundType.METAL).strength(1000.0F,1200.0F).pushReaction(PushReaction.IGNORE));
        CC_CHANCE = ccChance;
        SIZE = max(min(size,AOGNM_ORI-1),INERT*2);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(AOGNM_LEVEL,AOGNM_ORI).setValue(DIRECTION,0).setValue(ANGLE,ANGLE_MAX).setValue(GROW,false)
            .setValue(BLOCKPOS_AXIS,1).setValue(BLOCKPOS_AXIS_DIRECTION,true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(AOGNM_LEVEL,DIRECTION,ANGLE,GROW,BLOCKPOS_AXIS,BLOCKPOS_AXIS_DIRECTION);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston)
    {
        if(pLevel.isClientSide)return;
        if(pState.getValue(AOGNM_LEVEL) == AOGNM_ORI)
        {
            activate(pState,pLevel,pPos);
        }
        else if(pState.getValue(AOGNM_LEVEL) > INERT) {
            pLevel.scheduleTick(pPos, this, 8-pState.getValue(AOGNM_LEVEL)/4);
        }
        else
        {
            placeIOgnm(pState,(ServerLevel) pLevel,pPos);
        }
    }

    protected void placeIOgnm(BlockState aognmState, ServerLevel pLevel, BlockPos pPos)
    {
        if(ModBlock.IOGNM.block.get() instanceof InactiveOriginiumBlock inactive)
        {
            BlockState state =
                aognmState.getValue(AOGNM_LEVEL) > 20 ? inactive.CORE :
                aognmState.getValue(AOGNM_LEVEL) > 16 ? inactive.MIX4 :
                aognmState.getValue(AOGNM_LEVEL) > 12 ? inactive.MIX3 :
                aognmState.getValue(AOGNM_LEVEL) > 8 ? inactive.MIX2 :
                aognmState.getValue(AOGNM_LEVEL) > 4 ? inactive.MIX1 :
                inactive.SHELL;
            pLevel.setBlock(pPos, state,3);
        }
    }


    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom)
    {
        if(pState.getValue(AOGNM_LEVEL) == AOGNM_ORI)
        {
            activate(pState,pLevel,pPos);
            return;
        }
        int pDir = pState.getValue(DIRECTION);
        int pAng = pState.getValue(ANGLE);
        int pAxis = pState.getValue(BLOCKPOS_AXIS);
        boolean pAxisDir = pState.getValue(BLOCKPOS_AXIS_DIRECTION);

        if(pState.getValue(GROW))
        {
            expand(pState,pLevel,growPos(pPos,pRandom,pAxis,pAxisDir,pDir,pAng),pRandom,1,2,true);
            expand(pState,pLevel,growPos(pPos,pRandom,pAxis,!pAxisDir,pDir,pAng),pRandom,8,10,true);
        }
        expand(pState,pLevel,crossPos(pPos,pRandom,pAxis,pDir,1),pRandom,6,8,false);
        expand(pState,pLevel,stretchPos(pPos,pRandom,pAxis,pDir,0),pRandom,3,7,false);
        expand(pState,pLevel,stretchPos(pPos,pRandom,pAxis,pDir,1),pRandom,3,7,false);

        placeIOgnm(pState,pLevel,pPos);
    }

    protected BlockPos growPos(BlockPos pPos,RandomSource pRandom,int pAxis,boolean pAxisDir,int pDir,int pAng)
    {
        int pRan = pRandom.nextInt(0,ANGLE_MAX+1);
        return getAxis(pAxis).deal(
            pRan <= pAng ? crossPos(pPos,pRandom,pAxis,pDir,1) : pPos,
            List.of(pAxisDir ? Pair.of(AxisDir.above,1) : Pair.of(AxisDir.below,1))
        );
    }
    protected BlockPos crossPos(BlockPos pPos,RandomSource pRandom,int pAxis,int pDir,int pDis)
    {
        return getAxis(pAxis).deal(
            pPos,
            List.of(Pair.of(AxisDir.east,pDir&1),Pair.of(AxisDir.west,1-(pDir&1))
                ,Pair.of(AxisDir.south,pDir>>1),Pair.of(AxisDir.north,1-(pDir>>1))
            ));
    }
    protected BlockPos stretchPos(BlockPos pPos,RandomSource pRandom,int pAxis,int pDir,int pFlag)
    {
        int fg = pFlag&1;
        return getAxis(pAxis).deal(
            pPos,
            List.of(Pair.of(AxisDir.east,(1-(pDir&1))&fg),Pair.of(AxisDir.west,((pDir&1))&fg)
                ,Pair.of(AxisDir.south,(1-(pDir >> 1))&(1-fg)),Pair.of(AxisDir.north,(pDir>>1)&(1-fg))
            ));
    }

    protected void activate(BlockState pState, Level pLevel, BlockPos pPos)
    {
        RandomSource pRandom = pLevel.getRandom();
        Pair<Integer,Boolean> pAttachAxis = attachAxis(pLevel,pPos);
        pLevel.setBlock(pPos,
            pState.setValue(AOGNM_LEVEL,pRandom.nextInt(SIZE/2,SIZE+1))
                .setValue(DIRECTION,pRandom.nextInt(0,3+1))
                .setValue(ANGLE,pRandom.nextInt(0,ANGLE_MAX+1))
                .setValue(GROW,true)
                .setValue(BLOCKPOS_AXIS, pAttachAxis.getFirst())
                .setValue(BLOCKPOS_AXIS_DIRECTION,pAttachAxis.getSecond())
            ,3);
    }
    protected void expand(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, int declineFloor,int declineCeil,boolean grow)
    {
        int pDecline = pRandom.nextInt(declineFloor,declineCeil);
        if(!BlockStateUtil.isOgnm(pLevel.getBlockState(pPos)))
        {
            pLevel.setBlock(pPos,pState.setValue(AOGNM_LEVEL,max(pState.getValue(AOGNM_LEVEL)-pDecline,INERT)).setValue(GROW,grow),3);
        }
    }
}
