package com.azane.ogna.craft.catalyst;

import com.azane.ogna.block.CatalystBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.Map;

/**
 * 催化剂扫描器，用于扫描指定范围内的催化剂方块
 * @author azaneNH37 (2025-09-12)
 */
public class CatalystScanner
{
    private static final int SCAN_RADIUS_X = 2; // 5x5 area (center ± 2)
    private static final int SCAN_RADIUS_Y = 1; // 3 height (center ± 1)
    private static final int SCAN_RADIUS_Z = 2; // 5x5 area (center ± 2)

    /**
     * 扫描指定位置周围的催化剂方块
     * @param level 世界
     * @param centerPos 中心位置
     * @return 催化剂类型和数量的映射
     */
    public static Map<CatalystBlock.Type, Integer> scanCatalysts(Level level, BlockPos centerPos) {
        Map<CatalystBlock.Type, Integer> catalystCounts = new EnumMap<>(CatalystBlock.Type.class);

        // 扫描5x3x5范围
        for (int dx = -SCAN_RADIUS_X; dx <= SCAN_RADIUS_X; dx++) {
            for (int dy = -SCAN_RADIUS_Y; dy <= SCAN_RADIUS_Y; dy++) {
                for (int dz = -SCAN_RADIUS_Z; dz <= SCAN_RADIUS_Z; dz++) {
                    BlockPos scanPos = centerPos.offset(dx, dy, dz);
                    if (scanPos.equals(centerPos)) {
                        continue;
                    }

                    BlockState blockState = level.getBlockState(scanPos);
                    Block block = blockState.getBlock();

                    if (block instanceof CatalystBlock catalystBlock) {
                        CatalystBlock.Type type = catalystBlock.type;
                        catalystCounts.merge(type, 1, Integer::sum);
                    }
                }
            }
        }

        return catalystCounts;
    }

    /**
     * 检查催化剂需求是否被满足
     * @param level 世界
     * @param centerPos 中心位置
     * @param requirement 催化剂需求
     * @return 是否满足需求
     */
    public static boolean checkRequirement(Level level, BlockPos centerPos, CatalystRequirement requirement) {
        if (requirement.isEmpty()) {
            return true;
        }

        Map<CatalystBlock.Type, Integer> available = scanCatalysts(level, centerPos);
        return requirement.isSatisfiedBy(available);
    }

    /**
     * 获取扫描区域的信息（用于调试）
     */
    public static String getScanAreaInfo() {
        return String.format("Scan area: %dx%dx%d (center excluded)",
            SCAN_RADIUS_X * 2 + 1,
            SCAN_RADIUS_Y * 2 + 1,
            SCAN_RADIUS_Z * 2 + 1);
    }
}