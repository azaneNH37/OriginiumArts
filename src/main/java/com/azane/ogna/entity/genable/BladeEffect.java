package com.azane.ogna.entity.genable;

import com.azane.ogna.Config;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.manager.BladeEffectAABBManager;
import com.azane.ogna.lib.EdataSerializer;
import com.azane.ogna.registry.EntityRegistry;
import com.azane.ogna.resource.service.CommonDataService;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.UUID;

public class BladeEffect extends Entity implements GeoEntity, TraceableEntity
{
    public static final String FAILSAFE_ID = "ogna:default_blade_effect";
    //geckolib
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_BLADE = RawAnimation.begin().thenPlay("blade.active");

    //self-build data
    private IBladeEffect sharedDataBase = null;

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
    @VisibleForDebug
    public static final EntityDataAccessor<AABB> ATTACK_AREA = SynchedEntityData.defineId(BladeEffect.class, EdataSerializer.AA_BB);


    public BladeEffect(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static BladeEffect createBlade(Level pLevel, @NotNull Entity owner, ResourceLocation rl)
    {
        BladeEffect blade = new BladeEffect(EntityRegistry.BLADE_EFFECT.get(), pLevel);
        blade.setOwner(owner);
        blade.setDataBase(rl);
        updateTransform(blade);
        return blade;
    }
    public static void createTransform(BladeEffect blade)
    {
        if(blade.transform == null)
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
        if(Config.isDebughitbox())
            blade.getEntityData().set(ATTACK_AREA, blade.transform.aabb());
    }

    @Override
    public void tick()
    {
        super.tick();
        if(this.level().isClientSide())
        {
            if(age == 0)
            {
                FX fx = FXHelper.getFX(Objects.requireNonNull(ResourceLocation.tryBuild(OriginiumArts.MOD_ID, "roman")));
                var effect = new EntityEffect(fx, this.level(), this, EntityEffect.AutoRotate.FORWARD);
                effect.start();
            }
            age++;
            return;
        }
        if(this.getDataBase().getHitFrame().contains(age))
        {
            this.dealDamageToTargets();
        }
        if(age > this.getDataBase().getLife())
        {
            this.discard();
        }
        age++;
    }

    public void dealDamageToTargets()
    {
        if(this.level().isClientSide())
            return;
    }

    public void setDataBase(@Nullable ResourceLocation rl)
    {
        if(rl == null)
        {
            if(sharedDataBase != null)
                return;
            rl = ResourceLocation.parse(getEntityData().get(DATABASE_ID));
        }
        else
            this.getEntityData().set(DATABASE_ID, rl.toString());
        sharedDataBase = CommonDataService.get().getBladeEffect(rl);
        if(sharedDataBase == null)
        {
            this.getEntityData().set(DATABASE_ID, FAILSAFE_ID);
            sharedDataBase = CommonDataService.get().getBladeEffect(ResourceLocation.parse(FAILSAFE_ID));
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
        if(Config.isDebughitbox())
            this.getEntityData().define(ATTACK_AREA, AABB.ofSize(Vec3.ZERO, 0, 0,0));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound)
    {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.cachedOwner = null;
        }
        if (pCompound.contains("Age", 3)) {
            this.age = pCompound.getInt("Age");
        } else {
            this.age = 0;
        }
        if (pCompound.contains("DatabaseId", 8)) {
            this.getEntityData().set(DATABASE_ID, pCompound.getString("DatabaseId"));
        } else {
            this.getEntityData().set(DATABASE_ID, FAILSAFE_ID);
        }
        setDataBase(ResourceLocation.parse(this.getEntityData().get(DATABASE_ID)));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound)
    {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putString("DatabaseId", this.getEntityData().get(DATABASE_ID));
        pCompound.putInt("Age", this.age);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"controller",0, state -> state.setAndContinue(ANIM_BLADE)));
    }
}
