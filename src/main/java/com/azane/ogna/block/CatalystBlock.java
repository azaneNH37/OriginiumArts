package com.azane.ogna.block;

import com.azane.ogna.registry.ModBlock;
import lombok.AllArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class CatalystBlock extends Block
{
    @AllArgsConstructor
    public enum Type {
        RED(()->ModBlock.CATALYST_RED),
        BLUE(()->ModBlock.CATALYST_BLUE),
        GREEN(()->ModBlock.CATALYST_GREEN),
        WHITE(()->ModBlock.CATALYST_WHITE);

        public final Supplier<ModBlock.ItemBlock> itemBlock;
    }

    public final Type type;

    public CatalystBlock(Type type)
    {
        super(BlockBehaviour.Properties.of().strength(1));
        this.type = type;
    }
}
