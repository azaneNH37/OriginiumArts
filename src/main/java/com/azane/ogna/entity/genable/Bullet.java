package com.azane.ogna.entity.genable;

import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.SelectorType;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxBlockEffectTriggerPacket;
import com.azane.ogna.network.to_client.FxEntityEffectTriggerPacket;
import com.azane.ogna.registry.EntityRegistry;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.OgnaFxHelper;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Bullet extends Projectile implements GeoEntity, IEntityAdditionalSpawnData
{
    public static final EntityType<Bullet> TYPE = EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC).noSummon().noSave().fireImmune().sized(0.1F, 0.1F).clientTrackingRange(5).updateInterval(5).setShouldReceiveVelocityUpdates(false).build("bullet");

    @Getter
    private IBullet dataBase;

    //geckolib
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_BULLET = RawAnimation.begin().thenPlay("bullet.active");

    private int life;
    private Vec3 startPos;

    private CombatUnit combatUnit;
    private SelectorUnit selectorUnit;


    public Bullet(EntityType<? extends Bullet> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public Bullet(LivingEntity shooter, Level level,ResourceLocation dataBase,CombatUnit combatUnit,SelectorUnit selectorUnit) {
        this(EntityRegistry.BULLET.get(), level);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setOwner(shooter);
        this.dataBase = CommonDataService.get().getBullet(dataBase);
        this.startPos = this.position();
        this.combatUnit = combatUnit;
        this.selectorUnit = selectorUnit;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();

        if(life == 0 && this.level().isClientSide())
        {
            OgnaFxHelper.extractFxUnit(getDataBase().getFxData(),FxData::getAwakeFx)
                .map(FxData.FxUnit::getId).map(FXHelper::getFX)
                .ifPresent(fx->{
                    var effect = new EntityEffect(fx, this.level(), this, EntityEffect.AutoRotate.FORWARD);
                    effect.setForcedDeath(true);
                    effect.start();
                });
        }

        if (++this.life >= getDataBase().getLife()) {
            this.discard();
            return;
        }

        // 距离检查
        if (this.startPos != null && this.position().distanceTo(this.startPos) > getDataBase().getRange()) {
            this.discard();
            return;
        }

        // 移动和碰撞检测
        Vec3 currentPos = this.position();
        Vec3 deltaMovement = this.getDeltaMovement();
        Vec3 nextPos = currentPos.add(deltaMovement);

        // 方块碰撞检测
        HitResult hitResult = this.level().clip(new ClipContext(
            currentPos, nextPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            this
        ));

        if (hitResult.getType() != HitResult.Type.MISS) {
            nextPos = hitResult.getLocation();
        }

        // 实体碰撞检测
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
            this.level(), this, currentPos, nextPos,
            this.getBoundingBox().expandTowards(deltaMovement).inflate(1.0D),
            this::canHitEntity
        );

        if (entityHit != null) {
            hitResult = entityHit;
        }

        // 处理碰撞
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        // 更新位置
        this.setPos(nextPos.x, nextPos.y, nextPos.z);

        // 应用重力和阻力
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x * 0.99D, movement.y - 0.005D, movement.z * 0.99D);

        // 更新旋转
        this.updateRotation();
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        DebugLogger.log("Bullet hit entity: " + result.getEntity().getName().getString());
        //TODO:网络包合并
        if(!this.level().isClientSide())
        {
            OgnaFxHelper.extractFxUnit(getDataBase().getFxData(),FxData::getEndFx)
                .map(FxData.FxUnit::getId).ifPresent(rl->{
                    OgnmChannel.DEFAULT.sendToWithinRange(
                        new FxBlockEffectTriggerPacket(rl,result.getEntity().getOnPos().above(),false),
                        (ServerLevel) level(),
                        this.getOnPos(),
                        128
                    );
                });
            var dmgSource = new ArkDamageSource(combatUnit,this,this.getOwner(),null);

            if(selectorUnit.getType() == SelectorType.SINGLE)
            {
                if(result.getEntity() instanceof LivingEntity living)
                    combatUnit.onHitEntity((ServerLevel) this.level(), living, selectorUnit, dmgSource);
            }
            else {
                selectorUnit.gatherMultiTargets((ServerLevel) this.level(),this.getBoundingBox(),(living)->true)
                    .forEach(living -> combatUnit.onHitEntity((ServerLevel) this.level(), living, selectorUnit, dmgSource));
            }
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        DebugLogger.log("Bullet hit block at: " + result.getBlockPos());
        this.discard();
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(dataBase.getId());
        buffer.writeBlockPos(BlockPos.containing(startPos));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        ResourceLocation id = additionalData.readResourceLocation();
        dataBase = CommonDataService.get().getBullet(id);
        startPos = Vec3.atCenterOf(additionalData.readBlockPos());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"default",0,
            state -> state.setAndContinue(ANIM_BULLET)
        ));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
