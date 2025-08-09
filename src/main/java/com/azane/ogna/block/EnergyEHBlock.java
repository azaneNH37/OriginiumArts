package com.azane.ogna.block;

import com.azane.ogna.block.entity.EnergyEHBlockEntity;
import com.azane.ogna.registry.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EnergyEHBlock extends BaseEntityBlock
{
    public EnergyEHBlock()
    {
        super(BlockBehaviour.Properties.of().strength(1F));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {return new EnergyEHBlockEntity(pPos,pState);}
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
    {
        return createTickerHelper(pBlockEntityType, ModBlockEntity.ENERGY_EH_ENTITY.get(), pLevel.isClientSide ? EnergyEHBlockEntity::clientTick : EnergyEHBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if(pLevel instanceof ServerLevel serverLevel)
        {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EnergyEHBlockEntity energyEH) {
                energyEH.onPlayerUse(pPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
