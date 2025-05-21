package com.azane.ogna.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.renderer.layer.IColorable;
import com.azane.ogna.lib.EdataSerializer;
import com.azane.ogna.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.FastColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SlashEntity extends Entity implements GeoEntity, IColorable
{
    private static final int COLOR_WHITE = FastColor.ARGB32.color(0,255,255,255);
    //TODO: Well we can distinguish it between different slashes, could be lots of fun?
    private static final int ATTACK_TICK = 3;

    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(SlashEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(SlashEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<AABB> D_ATKAREA = SynchedEntityData.defineId(SlashEntity.class, EdataSerializer.AA_BB);

    private static final RawAnimation ANIM_SLASH = RawAnimation.begin().thenPlay("slash.normal");

    private final AnimatableInstanceCache animCache = GeckoLibUtil.createInstanceCache(this);


    private AABB attackArea = null;
    private LivingEntity creator = null;
    private int life = 0;


    public SlashEntity(EntityType<? extends Entity> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setNoGravity(true);

    }

    protected SlashEntity(EntityType<? extends Entity> pEntityType,Level pLevel,LivingEntity creator)
    {
        this(pEntityType,pLevel);
        this.setPos(creator.getX(),creator.getEyeY()-0.2D,creator.getZ());
        float pX = creator.getXRot(), pY = creator.getYRot();
        this.setYRot(pY);
        this.setXRot(pLevel.getRandom().nextInt(-45,45));
        updateAttackArea();
    }

    public static SlashEntity createSlash(Level pLevel, LivingEntity creator,int lifetime,int packedColor)
    {
        SlashEntity slash = new SlashEntity(EntityRegistry.SLASH.get(), pLevel,creator);
        slash.creator = creator;
        slash.life = lifetime;
        slash.entityData.set(LIFETIME,lifetime);
        slash.entityData.set(DATA_COLOR,packedColor);
        return slash;
    }



    protected void tickDespawn()
    {
        if(this.level().isClientSide())
            return;
        life--;
        if(life + ATTACK_TICK == getLifetime())
            applyDamageToTargets();
        if(life <= 0)
            this.discard();
    }

    @Override
    public void tick()
    {
        super.tick();
        tickDespawn();
    }

    protected void updateAttackArea()
    {
        double length = 3;
        double halfWidth = 1.8;
        Vec3 direction = Vec3.directionFromRotation(getXRot(), getYRot());
        OriginiumArts.LOGGER.warn(direction.toString());
        Vec3 start = position();
        Vec3 end = start.add(direction.scale(length));
        boolean face = Math.abs(direction.x)>Math.abs(direction.z);
        attackArea = new AABB(start, end).inflate(halfWidth * (face ? 0.1 : 1), 0.5, halfWidth* (!face ? 0.1 : 1));
        entityData.set(D_ATKAREA,attackArea);
        //OriginiumArts.LOGGER.warn("{}", attackArea);
    }
    private void applyDamageToTargets() {
        // 获取所有与碰撞箱相交的活体实体（排除自己和攻击发起者）
        List<LivingEntity> targets = level().getEntitiesOfClass(
            LivingEntity.class,
            attackArea,
            entity -> entity != creator
        );
        DamageSource damageSource = creator != null ?
            damageSources().playerAttack((Player) creator) :
            damageSources().magic();
        for (LivingEntity target : targets) {
            target.hurt(damageSource, 5.0F);
        }
    }

    @Override
    protected void defineSynchedData()
    {
        this.getEntityData().define(DATA_COLOR,COLOR_WHITE);
        this.getEntityData().define(LIFETIME,8);
        this.getEntityData().define(D_ATKAREA,new AABB(0,0,0,0,0,0));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"slashh",0,this::animSlashController));
    }

    protected <E extends SlashEntity> PlayState animSlashController(final AnimationState<E> event)
    {
        return event.setAndContinue(ANIM_SLASH);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return animCache;
    }

    @Override
    public int getPackedColor()
    {
        return entityData.get(DATA_COLOR);
    }
    public int getLifetime()
    {
        return entityData.get(LIFETIME);
    }
    public AABB getAttackArea()
    {
        return entityData.get(D_ATKAREA);
    }
}
