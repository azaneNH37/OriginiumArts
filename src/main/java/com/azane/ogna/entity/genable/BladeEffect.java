package com.azane.ogna.entity.genable;

import com.azane.ogna.OgnaConfig;
import com.azane.ogna.combat.data.CastContext;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.manager.BladeEffectAABBManager;
import com.azane.ogna.lib.EdataSerializer;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModEntity;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.OgnaFxHelper;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * @author azaneNH37 (2025-08-09)
 */
public class BladeEffect extends Entity implements GeoEntity, TraceableEntity
{
    public static final EntityType<BladeEffect> TYPE = EntityType.Builder.<BladeEffect>of(BladeEffect::new, MobCategory.MISC).noSummon().noSave().fireImmune().sized(0.1F, 0.1F).clientTrackingRange(5).updateInterval(5).setShouldReceiveVelocityUpdates(false).build("blade_effect");
    public static final String FAILSAFE_ID = "ogna:default_blade_effect";
    //geckolib
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_BLADE = RawAnimation.begin().thenPlay("blade.active");

    //self-build data
    private IBladeEffect sharedDataBase = null;
    @Getter
    private CastContext castContext;

    //S data
    @Nullable
    private UUID ownerUUID = null;
    @Nullable
    private Entity cachedOwner = null;


    @Getter
    private int age = 0;

    private BladeEffectAABBManager.BladeTransform transform = null;

    //S->C data
    private static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(BladeEffect.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Vector3f> SCALE = SynchedEntityData.defineId(BladeEffect.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(BladeEffect.class, EntityDataSerializers.INT);
    @VisibleForDebug
    public static final EntityDataAccessor<AABB> ATTACK_AREA = SynchedEntityData.defineId(BladeEffect.class, EdataSerializer.AA_BB);


    private BladeEffect(EntityType<? extends BladeEffect> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public BladeEffect(CastContext castContext)
    {
        this(ModEntity.BLADE_EFFECT.get(), castContext.getServerLevel());
        this.castContext = castContext;
        castContext.setLinkedAttackEntity(this);
        setOwner(castContext.getCaster());
        setDataBase(castContext.getAtkEntityUnit().getId());
        updateTransform(this);
        this.getEntityData().set(DELAY,castContext.getAtkEntityUnit().getDelay());
        this.setInvisible(true);
    }

    public static void createTransform(BladeEffect blade)
    {
        blade.transform = blade.getDataBase().generateTransform(blade.getOwner());
    }
    public BladeEffectAABBManager.BladeTransform getOrCreateTransform()
    {
        if(this.transform == null)
            createTransform(this);
        return this.transform;
    }
    public static void updateTransform(BladeEffect blade)
    {
        createTransform(blade);
        blade.setPos(blade.transform.center());
        //float[] rotations = BladeEffectAABBManager.extractRotationFromDirection(blade.getOrCreateTransform().forward());
        blade.setRot(blade.transform.yRot(), blade.transform.xRot());
        blade.getEntityData().set(SCALE, blade.transform.renderScale().toVector3f());
        if(OgnaConfig.isDebughitbox())
            blade.getEntityData().set(ATTACK_AREA, blade.transform.aabb());
    }

    @Override
    public void tick()
    {
        super.tick();
        if(this.level().isClientSide())
        {
            if(age == getEntityData().get(DELAY))
            {
                OgnaFxHelper.extractFxUnit(getDataBase().getFxData(), FxData::getAwakeFx)
                    .map(FxData.FxUnit::getId).map(FXHelper::getFX)
                    .ifPresent(fx->{
                        var effect = new EntityEffect(fx, this.level(), this, EntityEffect.AutoRotate.FORWARD);
                        effect.setForcedDeath(true);
                        effect.start();
                    });
            }
        }
        else
        {
            if(age == getEntityData().get(DELAY))
            {
                setInvisible(false);
                updateTransform(this);
                triggerAnim("default","attack");
                SoundKeyData.SoundKeyUnit unit = getDataBase().getSoundData() == null ? null : getDataBase().getSoundData().getAwakeSound();
                if(unit != null)
                    SoundKeyData.getSound(unit).ifPresent(soundEvent ->
                        this.level().playSound(null,this.position().x, this.position().y, this.position().z,
                        soundEvent, SoundSource.PLAYERS, unit.getVolume(), unit.getPitch()));
            }
            if(this.getDataBase().getHitFrame().contains(age-getEntityData().get(DELAY)))
            {
                this.dealDamageToTargets();
            }
            if(age > this.getDataBase().getLife()+getEntityData().get(DELAY))
            {
                this.discard();
            }
        }
        age++;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}

    public void dealDamageToTargets()
    {
        if(this.level().isClientSide())
            return;
        castContext.gatherMultiTargets((ServerLevel) this.level(),getOrCreateTransform().aabb(),(living)->living != getOwner(),null).forEach(castContext::onHitEntity);
    }

    public void setDataBase(@Nullable ResourceLocation rl)
    {
        if(rl == null)
        {
            if(sharedDataBase != null)
                return;
            rl = RlHelper.parse(getEntityData().get(DATABASE_ID));
        }
        else
            this.getEntityData().set(DATABASE_ID, rl.toString());
        sharedDataBase = CommonDataService.get().getBladeEffect(rl);
        if(sharedDataBase == null)
        {
            this.getEntityData().set(DATABASE_ID, FAILSAFE_ID);
            sharedDataBase = CommonDataService.get().getBladeEffect(RlHelper.parse(FAILSAFE_ID));
        }
    }
    public IBladeEffect getDataBase()
    {
        if(sharedDataBase == null)
            setDataBase(null);
        return sharedDataBase;
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }

    }
    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    protected void defineSynchedData()
    {
        this.getEntityData().define(DATABASE_ID, FAILSAFE_ID);
        this.getEntityData().define(SCALE, new Vector3f(1, 1, 1));
        this.getEntityData().define(DELAY,0);
        if(OgnaConfig.isDebughitbox())
            this.getEntityData().define(ATTACK_AREA, AABB.ofSize(Vec3.ZERO, 0, 0,0));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"default",0,
            state -> PlayState.STOP
        ).triggerableAnim("attack",ANIM_BLADE));
    }
}
