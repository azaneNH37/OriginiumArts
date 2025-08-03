package com.azane.ogna.world.feature;

import com.azane.ogna.debug.log.DebugLogger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class OgnmFeature extends Feature<OgnmFeature.Config>
{
    public OgnmFeature() {super(Config.CODEC);}

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        Config config = context.config();

        //DebugLogger.log("place Feature");
        // 获取上方方块
        BlockState topBlock = config.topBlock().getState(random, origin);
        // 获取下方方块
        BlockState bottomBlock = config.bottomBlock().getState(random, origin);

        // 在原点放置上方方块
        setBlock(level, origin, topBlock);
        level.scheduleTick(origin, topBlock.getBlock(), level.getRandom().nextInt(10,30));
        //DebugLogger.log("scheduleTick at " + origin + " for " + topBlock.getBlock());
        // 在原点下方放置下方方块
        setBlock(level, origin.below(), bottomBlock);
        level.scheduleTick(origin.below(), bottomBlock.getBlock(), level.getRandom().nextInt(10,30));

        return true;
    }

    public record Config(WeightedStateProvider topBlock,
                         WeightedStateProvider bottomBlock) implements FeatureConfiguration
    {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WeightedStateProvider.CODEC.fieldOf("top_block").forGetter(Config::topBlock),
            WeightedStateProvider.CODEC.fieldOf("bottom_block").forGetter(Config::bottomBlock)
        ).apply(instance, Config::new));
    }
}
