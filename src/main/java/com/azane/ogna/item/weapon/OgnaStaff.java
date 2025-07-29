package com.azane.ogna.item.weapon;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.client.renderer.OgnaWeaponRenderer;
import com.azane.ogna.combat.util.CombatFirer;
import com.azane.ogna.genable.item.weapon.IDefaultOgnaWeaponDataBase;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.resource.service.ServerDataService;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
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
    private static final RawAnimation IDLE_SKILL = RawAnimation.begin().thenLoop("staff.idle.skill");
    private static final RawAnimation ATTACK_NORMAL = RawAnimation.begin().thenPlay("staff.attack.normal");
    private static final RawAnimation ATTACK_SKILL = RawAnimation.begin().thenPlay("staff.attack.skill");
    private static final RawAnimation SKILL_START = RawAnimation.begin().thenPlay("staff.skill.start");
    private static final RawAnimation SKILL_END = RawAnimation.begin().thenPlay("staff.skill.end");

    @Getter
    private final Map<Integer, String> animeHashMap = new ImmutableMap.Builder<Integer,String>()
        .put(IDLE.hashCode(),"staff.idle")
        .put(ATTACK_NORMAL.hashCode(),"staff.attack.normal")
        .put(ATTACK_SKILL.hashCode(),"staff.attack.skill")
        .put(IDLE_SKILL.hashCode(),"staff.idle.skill")
        .put(SKILL_START.hashCode(),"staff.skill.start")
        .put(SKILL_END.hashCode(),"staff.skill.end")
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
                event-> {
                    ItemStack stack = event.getData(DataTickets.ITEMSTACK);
                    if(IOgnaWeapon.isWeapon(stack))
                    {
                        IOgnaWeapon weapon = (IOgnaWeapon) stack.getItem();
                        return weapon.getWeaponCap(stack).getSkillCap().isActive() ?
                            event.setAndContinue(IDLE_SKILL) :
                            event.setAndContinue(IDLE);
                    }
                    return event.setAndContinue(IDLE);
                })
                .triggerableAnim("attack.normal", ATTACK_NORMAL)
                .triggerableAnim("attack.skill", ATTACK_SKILL)
                .triggerableAnim("skill.start", SKILL_START)
                .triggerableAnim("skill.end", SKILL_END)
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
        if(pUsedHand == InteractionHand.MAIN_HAND)
        {
            ItemStack stack = pPlayer.getMainHandItem();
            IOgnaWeaponCap weaponCap = getWeaponCap(stack);
            if(weaponCap.getSkillCap().getSkill() == null)
                weaponCap.getSkillCap().equipSkill(RlHelper.build(OriginiumArts.MOD_ID,"sk.d-locky"));
            else
                if(onSkillInvoke(pLevel,pPlayer,stack))
                {
                    if(pLevel instanceof ServerLevel serverLevel)
                        triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","skill.start");
                }
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public void onServerAttack(ItemStack stack, ServerPlayer pPlayer, AttackType attackType, long chargeTime)
    {
        super.onServerAttack(stack, pPlayer, attackType, chargeTime);
        Level pLevel = pPlayer.level();
        if (pLevel instanceof ServerLevel serverLevel)
        {
            IOgnaWeaponCap cap = getWeaponCap(stack);
            boolean isInSkill = cap.getSkillCap().isActive();
            cap.modifyCurrentEnergy(
                -cap.submitBaseAttrVal(ModAttribute.WEAPON_ENERGY_CONSUME.get(), pPlayer, stack),
                true,pPlayer,stack
            );
            if(isInSkill)
                triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","attack.skill");
            else
                triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getMainHandItem(), serverLevel), "default","attack.normal");

            if(!isInSkill || (cap.getSkillCap().getSkill() != null && !cap.getSkillCap().getSkill().onServerAttack(
                serverLevel, pPlayer,
                this, stack, attackType, chargeTime, true)))
            {
                CombatFirer.fireDefault(serverLevel, pPlayer,this, cap, stack, "normal", "normal");
            }
        }
    }
}
