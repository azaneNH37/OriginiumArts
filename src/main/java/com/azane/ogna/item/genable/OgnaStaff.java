package com.azane.ogna.item.genable;

import com.azane.ogna.client.renderer.weapon.OgnaWeaponRenderer;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.ArkDmgTypes;
import com.azane.ogna.combat.util.DmgCategory;
import com.azane.ogna.combat.util.SelectorType;
import com.azane.ogna.genable.item.weapon.IDefaultOgnaWeaponDataBase;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.resource.service.ServerDataService;
import com.azane.ogna.util.AtkEntityHelper;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OgnaStaff extends DefaultOgnaPolyWeapon implements IPolyItemDataBase<IStaffDataBase>
{
    /**
     * gecko动画
     */
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("staff.idle");
    private static final RawAnimation ATTACK_NORMAL = RawAnimation.begin().thenPlay("staff.attack.normal");
    private static final RawAnimation ATTACK_SKILL = RawAnimation.begin().thenPlay("staff.attack.skill");

    @Getter
    private final Map<Integer, String> animeHashMap = new ImmutableMap.Builder<Integer,String>()
        .put(IDLE.hashCode(),"staff.idle")
        .put(ATTACK_NORMAL.hashCode(),"staff.attack.normal")
        .put(ATTACK_SKILL.hashCode(),"staff.attack.skill")
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
            new AnimationController<>(this, DEFAULT_CONTROLLER,0,
                event-> event.setAndContinue(IDLE))
                .triggerableAnim("attack_normal", ATTACK_NORMAL)
                .triggerableAnim("attack_skill", ATTACK_SKILL)
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
        if(pLevel instanceof ServerLevel serverLevel)
        {
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","attack_skill");
            AtkEntityHelper.createDefaultBlade(serverLevel,(ServerPlayer) pPlayer,
                getDataBaseForStack(pPlayer.getMainHandItem()).getAtkEntities().getSkillAtkUnit(0),
                CombatUnit.of(
                    ArkDmgTypes.getHolder(ArkDmgTypes.DEFAULT),
                    pPlayer.getAttribute(Attributes.ATTACK_DAMAGE).getValue(),
                    getWeaponCap(pPlayer.getMainHandItem()).extractMatrices(Set.of(Attributes.ATTACK_DAMAGE)),
                    DmgCategory.ARTS),
                SelectorUnit.of(
                    SelectorType.AREA,
                    0D,1,
                    en->true
                )
                );
        }
        return super.use(pLevel,pPlayer,pUsedHand);
    }

    @Override
    public void onServerAttack(ItemStack stack, ServerPlayer pPlayer, AttackType attackType, long chargeTime)
    {
        super.onServerAttack(stack, pPlayer, attackType, chargeTime);
        Level pLevel = pPlayer.level();
        if (pLevel instanceof ServerLevel serverLevel)
        {
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","attack_normal");
            AtkEntityHelper.shootDefaultBullet(serverLevel, pPlayer,
                getDataBaseForStack(pPlayer.getMainHandItem()).getAtkEntities().getNormal(),
                CombatUnit.of(
                    ArkDmgTypes.getHolder(ArkDmgTypes.DEFAULT),
                    pPlayer.getAttribute(Attributes.ATTACK_DAMAGE).getValue(),
                    getWeaponCap(stack).extractMatrices(Set.of(Attributes.ATTACK_DAMAGE)),
                    DmgCategory.ARTS),
                SelectorUnit.of(
                    SelectorType.AREA,
                    2D,1,
                    en->true
                ));
        }
    }
}
