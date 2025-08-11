package com.azane.ogna.util;

import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.network.to_client.FxBlockEffectTriggerPacket;
import com.azane.ogna.network.to_client.FxEntityEffectTriggerPacket;
import com.lowdragmc.photon.client.fx.BlockEffect;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author azaneNH37 (2025-07-22)
 */
public final class OgnaFxHelper
{
    public static Optional<FxData.FxUnit> extractFxUnit(@Nullable FxData fxData, Function<FxData, FxData.FxUnit> mapper)
    {
        return Optional.ofNullable(fxData).map(mapper);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTriggerEntityFx(FxEntityEffectTriggerPacket triggerPacket)
    {
        ClientLevel level = Minecraft.getInstance().level;
        if(level != null)
        {
            Entity entity = level.getEntity(triggerPacket.getEntityID());
            if(entity != null)
            {
                FX fx = FXHelper.getFX(triggerPacket.getFx());
                if(fx != null)
                {
                    var effect = new EntityEffect(fx, level,entity, EntityEffect.AutoRotate.NONE);
                    effect.setForcedDeath(triggerPacket.isForceDead());
                    effect.start();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTriggerBlockEffectFx(FxBlockEffectTriggerPacket triggerPacket)
    {
        ClientLevel level = Minecraft.getInstance().level;
        if(level != null)
        {
            BlockPos pos = triggerPacket.getBlockPos();
            FX fx = FXHelper.getFX(triggerPacket.getFx());
            if(fx != null)
            {
                var effect = new BlockEffect(fx,level,pos);
                effect.setForcedDeath(triggerPacket.isForceDead());
                effect.start();
            }
        }
    }
}
