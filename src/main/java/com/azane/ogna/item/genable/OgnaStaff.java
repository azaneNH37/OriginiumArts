package com.azane.ogna.item.genable;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.client.renderer.weapon.OgnaWeaponRenderer;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.item.IStaffDataBase;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.resource.service.ServerDataService;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OgnaStaff extends OgnaWeapon implements IPolyItemDataBase<IStaffDataBase>
{
    public static final String DEFAULT_CONTROLLER = "default";

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("staff.idle");
    private static final RawAnimation ATTACK_SWING1 = RawAnimation.begin().thenPlay("staff.attack.swing1");
    private static final RawAnimation ATTACK_SWING2 = RawAnimation.begin().thenPlay("staff.attack.swing2");

    private final Map<Integer, String> animeHashMap = new ImmutableMap.Builder<Integer,String>()
        .put(IDLE.hashCode(),"staff.idle")
        .put(ATTACK_SWING1.hashCode(),"staff.attack.swing1")
        .put(ATTACK_SWING2.hashCode(),"staff.attack.swing2")
        .build();

    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    @Getter
    private final Class<IStaffDataBase> dataBaseType = IStaffDataBase.class;
    //运行时根据mc机制特定Item类可以确保只有一个
    @Getter
    private final Map<ResourceLocation, IStaffDataBase> databaseCache = new ConcurrentHashMap<>();


    public OgnaStaff()
    {
        super(new Properties().stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(
            new AnimationController<>(this, DEFAULT_CONTROLLER,0, event->{
                return event.setAndContinue(IDLE);
            }).triggerableAnim("attack_swing2",ATTACK_SWING2)
        );
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (pLevel instanceof ServerLevel serverLevel)
        {
            RandomSource rand = serverLevel.getRandom();
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), serverLevel), "default","attack_swing2");
            //pLevel.addFreshEntity(SlashEntity.createSlash(serverLevel,pPlayer,12,
            //    FastColor.ARGB32.color(255,rand.nextInt(150,255),rand.nextInt(150,255),rand.nextInt(150,255))));
            //pLevel.addFreshEntity(BladeEffect.createBlade(pLevel,pPlayer, ResourceLocation.tryBuild(OriginiumArts.MOD_ID,"blade-alpha")));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }


    @Override
    public ResourceLocation getModel(ItemStack stack)
    {
        IStaffDataBase dataBase = getDataBaseForStack(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getModel).orElse(dataBase.getId());
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack)
    {
        IStaffDataBase dataBase = getDataBaseForStack(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getTexture).orElse(dataBase.getId());
    }

    @Override
    public ResourceLocation getAnimation(ItemStack stack)
    {
        IStaffDataBase dataBase = getDataBaseForStack(stack);
        return Optional.ofNullable(dataBase.getGeckoAsset()).map(GeckoAssetData::getAnimation).orElse(dataBase.getId());
    }

    @Override
    public Datums getCurrentAnimeDatums(ItemStack stack, ItemDisplayContext context,int animeHash)
    {
        return getDataBaseForStack(stack).getAnimeDatum(context,animeHashMap.getOrDefault(animeHash,"unknown"));
    }

    @Override
    public String getControllerName(ItemStack stack){ return DEFAULT_CONTROLLER; }

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

    @Override
    public OgnaStaff getItem(){return this;}


    @Override
    public boolean isDataBaseForStack(ItemStack itemStack)
    {
        return isThisGenItem(itemStack);
    }

    public static NonNullList<ItemStack> fillCreativeTab() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ServerDataService.get().getAllStaffs().stream().sorted((r, i)->r.getKey().getPath().hashCode()).forEach(entry->{
            stacks.add(entry.getValue().buildItemStack(1));
        });
        return stacks;
    }
}
