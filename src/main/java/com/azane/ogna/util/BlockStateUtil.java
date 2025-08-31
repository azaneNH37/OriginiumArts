package com.azane.ogna.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * @author azaneNH37 (2025/8/31)
 */
public class BlockStateUtil
{
    public static final int AOGNM_ORI = 32;

    public static final IntegerProperty AOGNM_LEVEL = IntegerProperty.create("alevel",0,AOGNM_ORI);
    public static final IntegerProperty IOGNM_LEVEL = IntegerProperty.create("ilevel",0,5);
    public static final IntegerProperty OGNM_CRYSTAL_LEVEL = IntegerProperty.create("clevel",0,5);

    public static boolean isOgnm(BlockState state)
    {
        return state.hasProperty(AOGNM_LEVEL) || state.hasProperty(IOGNM_LEVEL) || state.hasProperty(OGNM_CRYSTAL_LEVEL);
    }
}
