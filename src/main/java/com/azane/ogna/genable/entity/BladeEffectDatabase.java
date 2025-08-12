package com.azane.ogna.genable.entity;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

import static com.azane.ogna.genable.manager.BladeEffectAABBManager.*;

/**
 * 用于处理一种无法移动，碰撞箱可在实体创建时确定的范围性攻击特效
 * @author azaneNH37 (2025-08-04)
 */
@JsonClassTypeBinder(fullName = "blade",namespace = OriginiumArts.MOD_ID)
public class BladeEffectDatabase implements IBladeEffect
{
    public static final BladeEffectDatabase DEFAULT = new BladeEffectDatabase();

    //TODO: a fail-safe id
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("life")
    @Getter
    private int life = 10;
    //@SerializedName("range")
    //double range = 12;

    @SerializedName("hitframe")
    @Getter
    private Set<Integer> hitFrame = Set.of(0);

    @SerializedName("color")
    @Getter
    private int color = 0;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("transform")
    private BladeConfig config = BladeConfig.DEFAULT;

    @SerializedName("fx")
    @Getter
    @Nullable
    private FxData fxData;

    @SerializedName("sound")
    @Getter
    @Nullable
    private SoundKeyData soundData;

    //TODO: allow user to temporarily change the config
    @Override
    public BladeTransform generateTransform(Entity owner)
    {
        return createBladeTransform(owner,this.config);
    }

    @Override
    public <T extends Entity> Predicate<T> generateFilter(BladeTransform transform)
    {
        return createEntityFilter(transform, this.config);
    }
}