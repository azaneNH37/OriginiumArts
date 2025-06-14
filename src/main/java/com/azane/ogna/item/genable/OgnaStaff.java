package com.azane.ogna.item.genable;

import com.azane.ogna.client.renderer.weapon.OgnaWeaponRenderer;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.genable.item.weapon.IDefaultOgnaWeaponDataBase;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.resource.service.ServerDataService;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OgnaStaff extends DefaultOgnaPolyWeapon implements IPolyItemDataBase<IStaffDataBase>
{
    /**
     * gecko动画
     */
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("staff.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("staff.attack");

    @Getter
    private final Map<Integer, String> animeHashMap = new ImmutableMap.Builder<Integer,String>()
        .put(IDLE.hashCode(),"staff.idle")
        .put(ATTACK.hashCode(),"staff.attack")
        .build();

    @Getter
    private final Class<IStaffDataBase> dataBaseType = IStaffDataBase.class;
    //运行时根据mc机制特定Item类可以确保只有一个
    @Getter
    private final Map<ResourceLocation, IStaffDataBase> databaseCache = new ConcurrentHashMap<>();

    @Override
    public IDefaultOgnaWeaponDataBase getDefaultDatabase(ItemStack stack)
    {
        return getDataBaseForStack(stack);
    }

    @Override
    public OgnaStaff getItem(){return this;}

    @Override
    public boolean isDataBaseForStack(ItemStack itemStack)
    {
        return isThisGenItem(itemStack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(
            new AnimationController<>(this, DEFAULT_CONTROLLER,0, event->{
                return event.setAndContinue(IDLE);
            }).triggerableAnim("attack",ATTACK)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions()
        {
            private OgnaWeaponRenderer<OgnaStaff> renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                if(renderer == null)
                {
                    renderer = new OgnaWeaponRenderer<>();
                }
                return renderer;
            }
        });
    }

    //TODO:这里可以发现创造模式下通过tab获取的所有数据库来源相同的itemStack共享一个uuid
    public static NonNullList<ItemStack> fillCreativeTab() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ServerDataService.get().getAllStaffs().stream().sorted((r, i)->r.getKey().getPath().hashCode()).forEach(entry->{
            stacks.add(entry.getValue().buildItemStack(1));
        });
        return stacks;
    }


    public OgnaStaff() { super(); }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (pLevel instanceof ServerLevel serverLevel)
        {
            RandomSource rand = serverLevel.getRandom();
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), serverLevel), "default","attack");
            //pLevel.addFreshEntity(SlashEntity.createSlash(serverLevel,pPlayer,12,
            //    FastColor.ARGB32.color(255,rand.nextInt(150,255),rand.nextInt(150,255),rand.nextInt(150,255))));
            pLevel.addFreshEntity(BladeEffect.createBlade(pLevel,pPlayer, getDataBaseForStack(pPlayer.getItemInHand(pUsedHand)).getAtkEntities().getNormal()));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onServerAttack(ItemStack stack, ServerPlayer pPlayer, AttackType attackType, long chargeTime)
    {
        super.onServerAttack(stack, pPlayer, attackType, chargeTime);
        Level pLevel = pPlayer.level();
        if (pLevel instanceof ServerLevel serverLevel)
        {
            RandomSource rand = serverLevel.getRandom();
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","attack");
            //pLevel.addFreshEntity(SlashEntity.createSlash(serverLevel,pPlayer,12,
            //    FastColor.ARGB32.color(255,rand.nextInt(150,255),rand.nextInt(150,255),rand.nextInt(150,255))));
            pLevel.addFreshEntity(BladeEffect.createBlade(pLevel,pPlayer, getDataBaseForStack(pPlayer.getMainHandItem()).getAtkEntities().getNormal()));
        }
    }
}
