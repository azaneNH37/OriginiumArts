package com.azane.ogna.block;

import com.azane.ogna.block.entity.CraftOCCBlockEntity;
import com.azane.ogna.block.entity.InjectEPTBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class InjectEPTBlock extends BaseEntityBlock
{
    public InjectEPTBlock()
    {
        super(Properties.of().strength(1F));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {return new InjectEPTBlockEntity(pPos,pState);}
    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if(pLevel instanceof ServerLevel serverLevel)
        {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof InjectEPTBlockEntity injectEPT) {
                injectEPT.onPlayerUse(pPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
