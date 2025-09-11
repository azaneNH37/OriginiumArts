package com.azane.ogna.entity.genable;

import com.azane.ogna.combat.data.CastContext;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.manager.BladeEffectAABBManager;
import com.azane.ogna.registry.ModEntity;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.OgnaFxHelper;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Function;

/**
 * @author azaneNH37 (2025-08-09)
 */
public class BladeEffect extends Entity implements GeoEntity, TraceableEntity, IEntityAdditionalSpawnData
{
    public static final EntityType<BladeEffect> TYPE = EntityType.Builder.<BladeEffect>of(BladeEffect::new, MobCategory.MISC).noSummon().noSave().fireImmune().sized(0.1F, 0.1F).clientTrackingRange(200).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("blade_effect");
    //geckolib
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_BLADE = RawAnimation.begin().thenPlay("blade.active");

    //self-build data
    @Getter
    private IBladeEffect dataBase = null;
    @Getter
    private CastContext castContext;

    @Getter
    private int age = 0;

    private BladeEffectAABBManager.BladeTransform transform;

    @Getter
    private Vector3f renderScale;
    @Getter
    private AABB attackArea;

    private BladeEffect(EntityType<? extends BladeEffect> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public BladeEffect(CastContext castContext)
    {
        this(ModEntity.BLADE_EFFECT.get(), castContext.getServerLevel());
        this.dataBase = CommonDataService.get().getBladeEffect(castContext.getAtkEntityUnit().getId());
        this.castContext = castContext;
        castContext.setLinkedAttackEntity(this);
        updateTransform();
    }

    protected void updateTransform()
    {
        this.transform = BladeEffectAABBManager.createBladeTransform(castContext.getCaster(),this.dataBase.getConfig(),castContext.getMoveUnit());
        this.setPos(this.transform.center());
        this.setRot(this.transform.yRot(), this.transform.xRot());
        this.renderScale = this.transform.renderScale().toVector3f();
        this.attackArea = this.transform.aabb();
    }

    @Override
    public void tick()
    {
        super.tick();
        if(this.level().isClientSide())
        {
            if(age == 0)
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
            if(age == 0)
            {
                triggerAnim("default","attack");
                playSound(SoundKeyData::getAwakeSound);
            }
            if(this.getDataBase().getHitFrame().contains(age))
            {
                this.dealDamageToTargets();
            }
            if(age > this.getDataBase().getLife())
            {
                this.discard();
            }
        }
        age++;
    }

    public void dealDamageToTargets()
    {
        if(this.level().isClientSide())
            return;
        castContext.gatherMultiTargets((ServerLevel) this.level(),attackArea,(living)->living != getOwner(),null).forEach(castContext::onHitEntity);
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
    protected void defineSynchedData() {}
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}

    @Nullable
    public Entity getOwner() {
        return castContext == null ? null : castContext.getCaster();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"default",0,
            state -> PlayState.STOP
        ).triggerableAnim("attack",ANIM_BLADE));
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(dataBase.getId());
        buffer.writeDouble(renderScale.x);
        buffer.writeDouble(renderScale.y);
        buffer.writeDouble(renderScale.z);
        buffer.writeDouble(attackArea.minX);
        buffer.writeDouble(attackArea.minY);
        buffer.writeDouble(attackArea.minZ);
        buffer.writeDouble(attackArea.maxX);
        buffer.writeDouble(attackArea.maxY);
        buffer.writeDouble(attackArea.maxZ);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        ResourceLocation id = additionalData.readResourceLocation();
        dataBase = CommonDataService.get().getBladeEffect(id);
        renderScale = new Vector3f((float)additionalData.readDouble(),(float)additionalData.readDouble(),(float)additionalData.readDouble());
        attackArea = new AABB(
            additionalData.readDouble(),additionalData.readDouble(),additionalData.readDouble(),
            additionalData.readDouble(),additionalData.readDouble(),additionalData.readDouble()
        );
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
