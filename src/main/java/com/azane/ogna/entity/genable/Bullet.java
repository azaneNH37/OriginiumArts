package com.azane.ogna.entity.genable;

import com.azane.ogna.combat.data.*;
import com.azane.ogna.combat.util.SelectRule;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.entity.ITargetable;
import com.azane.ogna.lib.AABBHelper;
import com.azane.ogna.lib.ProjectileHelper;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxBlockEffectTriggerPacket;
import com.azane.ogna.registry.ModEntity;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.OgnaFxHelper;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author azaneNH37 (2025-08-09)
 */
public class Bullet extends Projectile implements GeoEntity, IEntityAdditionalSpawnData, ITargetable
{
    public static final EntityType<Bullet> TYPE = EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC).noSummon().noSave().fireImmune().sized(0.1F, 0.1F).clientTrackingRange(5).updateInterval(5).setShouldReceiveVelocityUpdates(false).build("bullet");

    @Getter
    private IBullet dataBase;
    @Getter
    private CastContext castContext;

    //geckolib
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_BULLET = RawAnimation.begin().thenPlay("bullet.active");

    private int life;
    private final Set<UUID> hitEntities = new HashSet<>();

    private MoveUnit moveUnit;

    private Bullet(EntityType<? extends Bullet> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public Bullet(CastContext castContext)
    {
        this(ModEntity.BULLET.get(), castContext.getServerLevel());
        castContext.setLinkedAttackEntity(this);
        this.setPos(castContext.getMoveUnit().getInitialPos());
        this.setOwner(castContext.getCaster());
        this.dataBase = CommonDataService.get().getBullet(castContext.getAtkEntityUnit().getId());
        this.moveUnit = castContext.getMoveUnit();
        this.castContext = castContext;
        this.shoot();
    }

    public void shoot()
    {
        shootFromRotation(castContext.getCaster(),
            (float) moveUnit.getXRot(), (float) moveUnit.getYRot(),
            0, dataBase.getSpeed()*moveUnit.getSpeedAmplifier(), 0);
    }

    @Override
    protected void defineSynchedData() {}

    @Nullable
    public Vec3 getActualTarget()
    {
        if (moveUnit.getTargetEntity() != null && moveUnit.getTargetEntity().isAlive()) {
            return moveUnit.getTargetEntity().position();
        }
        return moveUnit.getTargetPos();
    }

    @Override
    public void tick() {
        super.tick();

        // 初始特效音效附加
        if(life == 0)
        {
            if(this.level().isClientSide())
                OgnaFxHelper.extractFxUnit(getDataBase().getFxData(),FxData::getAwakeFx)
                    .map(FxData.FxUnit::getId).map(FXHelper::getFX)
                    .ifPresent(fx->{
                        var effect = new EntityEffect(fx, this.level(), this, EntityEffect.AutoRotate.FORWARD);
                        effect.setForcedDeath(true);
                        effect.start();
                    });
            else
                playSound(SoundKeyData::getAwakeSound);
        }

        //生命周期和距离检测
        if (++this.life >= getDataBase().getLife()) {
            this.discard();
            return;
        }
        if (this.moveUnit.getInitialPos() != null && this.position().distanceTo(this.moveUnit.getInitialPos()) > getDataBase().getRange()) {
            this.discard();
            return;
        }

        // 追踪
        this.setDeltaMovement(updateDeltaMovement(this.getDeltaMovement(), this.position(), this.moveUnit.getMinTrackingDistance(), this.moveUnit.getTurnRate()));

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

        var entityHits = ProjectileHelper.getEntitiesAlongPath(
            this.level(), this, currentPos, nextPos,
            AABBHelper.cube(Vec3.ZERO, dataBase.getSize()*moveUnit.getSizeAmplifier()),
            this::canHitEntity
            );

        if(!entityHits.isEmpty())
        {
            entityHits.forEach(this::onHitEntity);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, entityHits.get(0).position(), GameEvent.Context.of(this, null));
            if(!dataBase.isPenetrate())
                this.discard();
        }
        else if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        // 更新位置
        this.setPos(nextPos.x, nextPos.y, nextPos.z);

        // 应用重力和阻力
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x * 0.99D, movement.y - (dataBase.isGravity() ? 0.005D : 0D), movement.z * 0.99D);

        // 更新旋转
        this.updateRotation();
    }

    protected void onHitEntity(Entity result)
    {
        if(this.isRemoved())
            return;
        if(hitEntities.contains(result.getUUID()))
            return;
        hitEntities.add(result.getUUID());
        //TODO:网络包合并
        if(!this.level().isClientSide())
        {
            OgnaFxHelper.extractFxUnit(getDataBase().getFxData(),dataBase.isPenetrate() ? FxData::getHitFx : FxData::getEndFx)
                .map(FxData.FxUnit::getId).ifPresent(rl->{
                    OgnmChannel.DEFAULT.sendToWithinRange(
                        new FxBlockEffectTriggerPacket(rl,result.getOnPos().above(),false),
                        (ServerLevel) level(),
                        this.getOnPos(),
                        128
                    );
                });

            playSound(SoundKeyData::getHitSound);

            if(!(result instanceof LivingEntity))
                castContext.onHitEntity(result);
            castContext.gatherMultiTargets((ServerLevel) this.level(),AABBHelper.cube(result.position(), moveUnit.getSizeAmplifier()), SelectRule.NULL.getFilter(),
                    result instanceof LivingEntity living ? living : null)
                .forEach(castContext::onHitEntity);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        //DebugLogger.log("Bullet hit block at: " + result.getBlockPos());
        playSound(SoundKeyData::getHitSound);
        this.discard();
    }

    protected void playSound(Function<SoundKeyData, SoundKeyData.SoundKeyUnit> func)
    {
        if(!this.level().isClientSide())
        {
            SoundKeyData.SoundKeyUnit unit = getDataBase().getSoundData() == null ? null : func.apply(getDataBase().getSoundData());
            if(unit != null)
                SoundKeyData.getSound(unit).ifPresent(soundEvent ->
                    this.level().playSound(null,this.position().x, this.position().y, this.position().z,
                        soundEvent, SoundSource.PLAYERS, unit.getVolume(), unit.getPitch()));
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(dataBase.getId());
        moveUnit.toBuffer(buffer);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        ResourceLocation id = additionalData.readResourceLocation();
        dataBase = CommonDataService.get().getBullet(id);
        moveUnit = MoveUnit.fromBuffer(additionalData, this.level());
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
