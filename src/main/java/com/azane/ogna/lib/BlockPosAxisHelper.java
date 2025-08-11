package com.azane.ogna.lib;

import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @author azaneNH37 (2025-08-03)
 */
public class BlockPosAxisHelper
{
    public enum CrossAxis{
        X(0),
        Y(1),
        Z(2);

        static {
            initX();
            initY();
            initZ();
        }

        public final int index;
        private final Map<AxisDir,BiFunction<BlockPos,Integer,BlockPos>> map = new HashMap<>();

        CrossAxis(int id)
        {
            this.index = id;
        }

        private static void initX()
        {
            X.map.put(AxisDir.above,BlockPos::east);
            X.map.put(AxisDir.below,BlockPos::west);
            X.map.put(AxisDir.east,BlockPos::south);
            X.map.put(AxisDir.west,BlockPos::north);
            X.map.put(AxisDir.south,BlockPos::above);
            X.map.put(AxisDir.north,BlockPos::below);
        }
        private static void initY()
        {
            Y.map.put(AxisDir.above,BlockPos::above);
            Y.map.put(AxisDir.below,BlockPos::below);
            Y.map.put(AxisDir.east,BlockPos::east);
            Y.map.put(AxisDir.west,BlockPos::west);
            Y.map.put(AxisDir.south,BlockPos::south);
            Y.map.put(AxisDir.north,BlockPos::north);
        }
        private static void initZ()
        {
            Z.map.put(AxisDir.above,BlockPos::south);
            Z.map.put(AxisDir.below,BlockPos::north);
            Z.map.put(AxisDir.east,BlockPos::above);
            Z.map.put(AxisDir.west,BlockPos::below);
            Z.map.put(AxisDir.south,BlockPos::east);
            Z.map.put(AxisDir.north,BlockPos::west);
        }

        public BlockPos deal(BlockPos pPos,List<Pair<AxisDir,Integer>> pOp)
        {
            for (Pair<AxisDir, Integer> pPair : pOp) {
                pPos = this.map.get(pPair.first).apply(pPos,pPair.second);
            }
            return pPos;
        }
        public BlockPos deal(BlockPos pPos,AxisDir pAxis,int pDis)
        {
            return this.map.get(pAxis).apply(pPos,pDis);
        }
    }
    public enum AxisDir{
        east(4),
        west(5),
        above(0),
        below(1),
        south(2),
        north(3);

        public final int index;

        AxisDir(int id)
        {
            this.index = id;
        }

        public static AxisDir getAxisDir(int id)
        {
            for(AxisDir ad : AxisDir.values())
            {
                if(id == ad.index)return ad;
            }
            return AxisDir.above;
        }
        public static List<AxisDir> getSurfaceNeighbour()
        {
            return List.of(AxisDir.east, AxisDir.west, AxisDir.south, AxisDir.north);
        }
    }

    public static final IntegerProperty BLOCKPOS_AXIS = IntegerProperty.create("bp_axis",0,2);
    public static final BooleanProperty BLOCKPOS_AXIS_DIRECTION = BooleanProperty.create("bp_axis_dir");

    public static CrossAxis getAxis(int p)
    {
        for(CrossAxis axis : CrossAxis.values())
        {
            if(p == axis.index)return axis;
        }
        return CrossAxis.Y;
    }
    public static AxisDir getAttachAxisDir(int axis,boolean dir)
    {
        switch (axis){
            case 0 -> {
                return dir ? AxisDir.east : AxisDir.west;
            }
            case 2 -> {
                return dir ? AxisDir.south : AxisDir.north;
            }
            default -> {
                return dir ? AxisDir.above : AxisDir.below;
            }
        }
    }
    public static Pair<Integer,Boolean> verticalAxis(int axis,boolean dir,AxisDir out)
    {
        int vAxis = (axis+out.index/2)%3;
        boolean vDir = out.index%2 == 0;
        return Pair.of(vAxis,vDir);
    }
    public static AxisDir getCompareAxisDir(Pair<Integer,Boolean> baseAxis,Pair<Integer,Boolean> followAxis)
    {
        return AxisDir.getAxisDir(((3+followAxis.first- baseAxis.first)%3)*2+((baseAxis.second != followAxis.second) ? 1:0));
    }

    public static Pair<Integer,Boolean> attachAxis(Level pLevel, BlockPos pPos)
    {
        int filled = 0;
        int digit = 1;
        int cross = 63;
        BiPredicate<Integer,Integer> filledCheck = ((var,target) -> (var&target) == target);

        for(AxisDir dir : AxisDir.values())
        {
            filled += pLevel.getBlockState(getAxis(1).deal(pPos,dir,1)).isAir() ? 0 : digit;
            digit *= 2;
        }
        for(int index=0;index<3;index++)
        {
            cross &= filledCheck.test(filled,(1<<2*index)) ? 63^(1<<2*index):63^(1<<(2*index+1));
            cross &= filledCheck.test(filled,(1<<(2*index+1))) ? 63^(1<<(2*index+1)):63^(1<<2*index);
        }
        if(cross == 0){
            RandomSource pRandom = pLevel.getRandom();
            return Pair.of(pRandom.nextInt(0,2+1),pRandom.nextBoolean());
        }
        if(filledCheck.test(cross,4) || filledCheck.test(cross,8)) {
            return Pair.of(CrossAxis.Y.index,filledCheck.test(cross,4));
        } else if (filledCheck.test(cross,1) || filledCheck.test(cross,2)) {
            return Pair.of(CrossAxis.X.index,filledCheck.test(cross,1));
        }else{
            return Pair.of(CrossAxis.Z.index,filledCheck.test(cross,16));
        }
    }
}
