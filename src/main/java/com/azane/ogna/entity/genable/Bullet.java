package com.azane.ogna.entity.genable;

import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.MoveUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.SelectRule;
import com.azane.ogna.combat.util.SelectorType;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.entity.ITargetable;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxBlockEffectTriggerPacket;
import com.azane.ogna.registry.ModEntity;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
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

/**
 * @author azaneNH37 (2025-08-09)
 */
public class Bullet extends Projectile implements GeoEntity, IEntityAdditionalSpawnData, ITargetable
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

    private final Set<UUID> hitEntities = new HashSet<>();

    private Vec3 targetPos;
    private Entity targetEntity;
    private float minTrackingDistance = 0.2F;
    private float turnRate = 0.35F;

    private CombatUnit combatUnit;
    private SelectorUnit selectorUnit;


    public Bullet(EntityType<? extends Bullet> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public Bullet(LivingEntity shooter, Level level,ResourceLocation dataBase,CombatUnit combatUnit,SelectorUnit selectorUnit) {
        this(ModEntity.BULLET.get(), level);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setOwner(shooter);
        this.dataBase = CommonDataService.get().getBullet(dataBase);
        this.startPos = this.position();
        this.combatUnit = combatUnit;
        this.selectorUnit = selectorUnit;
    }
    //TODO: 将运动信息收集至MoveUnit中
    public Bullet(LivingEntity shooter, Level level, ResourceLocation dataBase, CombatUnit combatUnit, SelectorUnit selectorUnit, MoveUnit moveUnit)
    {
        this(shooter, level, dataBase, combatUnit, selectorUnit);
        this.targetEntity = moveUnit.getTargetEntity();
    }

    @Override
    public AABB getBoundingBox()
    {
        return super.getBoundingBox();
    }

    @Override
    protected void defineSynchedData() {}

    @Nullable
    public Vec3 getActualTarget()
    {
        if (targetEntity != null && targetEntity.isAlive()) {
            return targetEntity.position();
        }
        return targetPos;
    }

    @Override
    public void tick() {
        super.tick();

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
            {
                SoundKeyData.SoundKeyUnit unit = getDataBase().getSoundData() == null ? null : getDataBase().getSoundData().getAwakeSound();
                if(unit != null)
                    SoundKeyData.getSound(unit).ifPresent(soundEvent ->
                        this.level().playSound(null,this.position().x, this.position().y, this.position().z,
                            soundEvent, SoundSource.PLAYERS, unit.getVolume(), unit.getPitch()));
            }
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
        //DebugLogger.log("side:{}entity:{}",this.level().isClientSide,targetEntity != null);
        this.setDeltaMovement(updateDeltaMovement(this.getDeltaMovement(), this.position(), minTrackingDistance, turnRate));

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
            this.getBoundingBox().expandTowards(deltaMovement).inflate(3.0D),
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
        this.setDeltaMovement(movement.x * 0.99D, movement.y - (dataBase.isGravity() ? 0.005D : 0D), movement.z * 0.99D);

        // 更新旋转
        this.updateRotation();
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if(hitEntities.contains(result.getEntity().getUUID()))
            return;
        hitEntities.add(result.getEntity().getUUID());
        //DebugLogger.log("Bullet hit entity: " + result.getEntity().getName().getString());
        //TODO:网络包合并
        if(!this.level().isClientSide())
        {
            OgnaFxHelper.extractFxUnit(getDataBase().getFxData(),dataBase.isPenetrate() ? FxData::getHitFx : FxData::getEndFx)
                .map(FxData.FxUnit::getId).ifPresent(rl->{
                    OgnmChannel.DEFAULT.sendToWithinRange(
                        new FxBlockEffectTriggerPacket(rl,result.getEntity().getOnPos().above(),false),
                        (ServerLevel) level(),
                        this.getOnPos(),
                        128
                    );
                });

            SoundKeyData.SoundKeyUnit unit = getDataBase().getSoundData() == null ? null : getDataBase().getSoundData().getHitSound();
            if(unit != null)
            {
                SoundKeyData.getSound(unit).ifPresent(soundEvent ->
                    this.level().playSound(null,this.position().x, this.position().y, this.position().z,
                        soundEvent, SoundSource.PLAYERS, unit.getVolume(), unit.getPitch()));
                //DebugLogger.log(unit.getSoundKey());
            }

            var dmgSource = new ArkDamageSource(combatUnit,this,this.getOwner(),null);

            if(selectorUnit.getType() == SelectorType.SINGLE)
            {
                if(result.getEntity() instanceof LivingEntity living)
                {
                    if(selectorUnit.getFilter().test(living))
                        combatUnit.onHitEntity((ServerLevel) this.level(), living, selectorUnit, dmgSource);
                }
            }
            else {
                selectorUnit.gatherMultiTargets((ServerLevel) this.level(),this.getBoundingBox(), SelectRule.NULL.getFilter())
                    .forEach(living -> combatUnit.onHitEntity((ServerLevel) this.level(), living, selectorUnit, dmgSource));
            }
        }
        if(!dataBase.isPenetrate())
            this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        //DebugLogger.log("Bullet hit block at: " + result.getBlockPos());
        if(!this.level().isClientSide())
        {
            SoundKeyData.SoundKeyUnit unit = getDataBase().getSoundData() == null ? null : getDataBase().getSoundData().getHitSound();
            if(unit != null)
                SoundKeyData.getSound(unit).ifPresent(soundEvent ->
                    this.level().playSound(null,this.position().x, this.position().y, this.position().z,
                        soundEvent, SoundSource.PLAYERS, unit.getVolume(), unit.getPitch()));
        }
        this.discard();
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(dataBase.getId());
        buffer.writeBlockPos(BlockPos.containing(startPos));
        if(targetEntity != null)
        {
            buffer.writeBoolean(true);
            buffer.writeInt(targetEntity.getId());
        }
        else
        {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        ResourceLocation id = additionalData.readResourceLocation();
        dataBase = CommonDataService.get().getBullet(id);
        startPos = Vec3.atCenterOf(additionalData.readBlockPos());
        if(additionalData.readBoolean())
        {
            int targetId = additionalData.readInt();
            targetEntity = this.level().getEntity(targetId);
            if(targetEntity == null || !targetEntity.isAlive())
                targetEntity = null;
        }
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
