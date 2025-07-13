package com.azane.ogna.item;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.client.renderer.StaffRenderer;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.lib.RlHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

public class StaffItem extends Item implements GeoItem
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("staff.idle");
    private static final RawAnimation ATTACK_SWING1 = RawAnimation.begin().thenPlay("staff.attack.swing1");
    private static final RawAnimation ATTACK_SWING2 = RawAnimation.begin().thenPlay("staff.attack.swing2");

    private final Map<Integer, Datums> datumsMap = new ImmutableMap.Builder<Integer,Datums>()
        .put(IDLE.hashCode(),Datums.THIRD_PLAYER_RIGHT_HAND)
        .put(ATTACK_SWING1.hashCode(),Datums.THIRD_PLAYER_FRONT)
        .put(ATTACK_SWING2.hashCode(),Datums.THIRD_PLAYER_FRONT)
        .build();

    public Datums gainAnimeDatums(int hash)
    {
        return datumsMap.getOrDefault(hash,Datums.NONE);
    }

    public StaffItem()
    {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(
            new AnimationController<>(this, "default",0,event->{
                return event.setAndContinue(IDLE);
            }).triggerableAnim("attack_swing1",ATTACK_SWING2)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (pLevel instanceof ServerLevel serverLevel)
        {
            RandomSource rand = serverLevel.getRandom();
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), serverLevel), "default","attack_swing1");
            //pLevel.addFreshEntity(SlashEntity.createSlash(serverLevel,pPlayer,12,
            //    FastColor.ARGB32.color(255,rand.nextInt(150,255),rand.nextInt(150,255),rand.nextInt(150,255))));
            pLevel.addFreshEntity(BladeEffect.createBlade(pLevel,pPlayer, RlHelper.build(OriginiumArts.MOD_ID,"blade-alpha")));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions()
        {
            private StaffRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                if(renderer == null)
                {
                    renderer = new StaffRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
