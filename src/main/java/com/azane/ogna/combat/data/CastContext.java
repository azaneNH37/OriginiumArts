package com.azane.ogna.combat.data;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.attr.AttrMatrix;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.util.DmgCategory;
import com.azane.ogna.combat.util.SelectorType;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.entity.genable.Bullet;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.util.ModUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 将所有攻击实体需要用到的数据打包的顶层类
 * @author azaneNH37 (2025/9/1)
 */
@Getter
public class CastContext
{
    /**
     * 攻击实体数据
     */
    private final AtkEntityData.AtkUnit atkEntityUnit;

    /**
     * 环境数据
     */
    private final ServerLevel serverLevel;
    private final LivingEntity caster;
    private final boolean isPlayer;

    /**
     * 武器数据
     */
    private final IOgnaWeapon weapon;
    private final IOgnaWeaponCap weaponCap;
    private final ItemStack weaponStack;

    /**
     * 伤害数据
     */
    private final DmgDataSet.DamageData dmgDataSet;
    private final double baseVal;
    private final Predicate<LivingEntity> baseFilter;

    /**
     * 攻击实体移动行为
     */
    private final MoveUnit moveUnit;

    private CastContext(AtkEntityData.AtkUnit attackEntityUnit,
                        ServerLevel serverLevel, LivingEntity caster, boolean isPlayer,
                        IOgnaWeapon weapon, IOgnaWeaponCap weaponCap, ItemStack weaponStack,
                        DmgDataSet.DamageData dmgDataSet, double baseVal, Predicate<LivingEntity> baseFilter,
                        MoveUnit moveUnit)
    {
        this.atkEntityUnit = attackEntityUnit;
        this.serverLevel = serverLevel;
        this.caster = caster;
        this.isPlayer = isPlayer;
        this.weapon = weapon;
        this.weaponCap = weaponCap;
        this.weaponStack = weaponStack;
        this.dmgDataSet = dmgDataSet;
        this.baseVal = baseVal;
        this.baseFilter = baseFilter;
        this.moveUnit = moveUnit;
    }

    //动态数据
    @Setter
    private Entity linkedAttackEntity;
    /**
     *  攻击实体attrMap修正（不会在锁定他人时清空）
     */
    private final AttrMap atkEntityAttrMap = new AttrMap();
    /**
     *  目标attrMap修正（会在锁定他人时清空）
     */
    private final AttrMap targetAttrMap = new AttrMap();

    public Entity createAttackEntity()
    {
        String type = atkEntityUnit.getAtkEntityType();
        Entity entity;
        switch (type)
        {
            case "blade" -> {
                entity = new BladeEffect(this);
            }
            case "bullet" -> {
                entity = new Bullet(this);
            }
            default -> throw new IllegalArgumentException("Unknown AtkEntity type: " + type);
        }
        serverLevel.addFreshEntity(entity);
        return entity;
    }

    public ISkill getSkill()
    {
        return weaponCap.getSkillCap().getSkill();
    }

    public AttrMatrix getMatrix(Attribute attribute)
    {
        return weaponCap.extractMatrices(Set.of(attribute)).get(attribute);
    }

    public List<LivingEntity> gatherMultiTargets(ServerLevel level, AABB basis, Predicate<LivingEntity> tester,@Nullable LivingEntity hitResult)
    {
        if(dmgDataSet.getSelectorType() == SelectorType.SINGLE)
        {
            if(hitResult != null && baseFilter.and(tester).test(hitResult))
                return List.of(hitResult);
            else
                return List.of();
        }
        List<LivingEntity> raw = level.getEntitiesOfClass(LivingEntity.class,basis.inflate(dmgDataSet.getRange()),tester.and(baseFilter));
        if(dmgDataSet.getSelectorType() == SelectorType.AREA)
            return raw;
        Collections.shuffle(raw);
        return raw.subList(0,Math.min(dmgDataSet.getHitCount(),raw.size()));
    }

    public void onHitEntity(LivingEntity target)
    {
        weaponCap.getChipSet().gather(ChipTiming.ON_HIT_ENTITY).forEach(chip -> chip.onImpactEntity(target,this));
        if(getSkill() != null)
            getSkill().onImpactEntity(target,this);
        var dmgSource = new ArkDamageSource(dmgDataSet.getDmgTypeHolder(false), linkedAttackEntity,caster, null);
        var unit = weaponCap.extractMatrices(ModUtil.COMBAT_ATTRIBUTES);
        AttrMatrix matrix = AttrMatrix.combine(true,
            unit.get(Attributes.ATTACK_DAMAGE),
            dmgDataSet.getDmgCategory() == DmgCategory.PHYSICS ? unit.get(ModAttribute.DAMAGE_PHYSICS.get()) :
                dmgDataSet.getDmgCategory() == DmgCategory.ARTS ? unit.get(ModAttribute.DAMAGE_ARTS.get()) :
                    AttrMatrix.UNIT_MATRIX
        );
        target.hurt(dmgSource, (float) matrix.submit(baseVal));
    }

    /**
     * 用于解决一些神秘实现喜欢绕开livingEntity用entity写生物
     * @param target
     */
    public void onHitEntity(Entity target)
    {
        var dmgSource = new ArkDamageSource(dmgDataSet.getDmgTypeHolder(false), linkedAttackEntity,caster, null);
        var unit = weaponCap.extractMatrices(ModUtil.COMBAT_ATTRIBUTES);
        AttrMatrix matrix = AttrMatrix.combine(true,
            unit.get(Attributes.ATTACK_DAMAGE),
            dmgDataSet.getDmgCategory() == DmgCategory.PHYSICS ? unit.get(ModAttribute.DAMAGE_PHYSICS.get()) :
                dmgDataSet.getDmgCategory() == DmgCategory.ARTS ? unit.get(ModAttribute.DAMAGE_ARTS.get()) :
                    AttrMatrix.UNIT_MATRIX
        );
        target.hurt(dmgSource, (float) matrix.submit(baseVal)*3.25f);
    }


    public static class Builder
    {
        private AtkEntityData.AtkUnit attackEntityUnit;
        private ServerLevel serverLevel;
        private LivingEntity caster;
        private boolean isPlayer;
        private IOgnaWeapon weapon;
        private IOgnaWeaponCap weaponCap;
        private ItemStack weaponStack;
        private DmgDataSet.DamageData dmgDataSet;
        private double baseVal;
        private Predicate<LivingEntity> baseFilter = e->true;
        private MoveUnit moveUnit;

        public Builder serverLevel(ServerLevel serverLevel)
        {
            this.serverLevel = serverLevel;
            return this;
        }

        public Builder caster(LivingEntity caster)
        {
            this.caster = caster;
            this.isPlayer = this.caster instanceof ServerPlayer;
            return this;
        }

        public Builder weapon(ItemStack weaponStack)
        {
            if(!IOgnaWeapon.isWeapon(weaponStack))
                throw new IllegalArgumentException("ItemStack is not an IOgnaWeapon");
            this.weaponStack = weaponStack;
            this.weapon = (IOgnaWeapon) weaponStack.getItem();
            this.weaponCap = this.weapon.getWeaponCap(weaponStack);
            return this;
        }

        /**
         * 在weapon后调用
         */
        public Builder attackEntityUnit(String AEunitID)
        {
            if(weapon == null)
                throw new IllegalStateException("weapon must be set before atkEntityUnit");
            ISkill skill = weaponCap.getSkillCap().getSkill();
            AtkEntityData w_ae = weapon.getDefaultDatabase(weaponStack).getAtkEntities();
            AtkEntityData s_ae = skill == null ? null : skill.getAtkEntities();
            this.attackEntityUnit = s_ae == null ? w_ae.getAtkUnit(AEunitID) :
                s_ae.hasAtkUnit(AEunitID) ? s_ae.getAtkUnit(AEunitID) : w_ae.getAtkUnit(AEunitID);
            return this;
        }

        /**
         * 在weapon后调用
         */
        public Builder damageDataSet(String DDunitID)
        {
            if(weapon == null)
                throw new IllegalStateException("weapon must be set before damageDataSet");
            ISkill skill = weaponCap.getSkillCap().getSkill();
            DmgDataSet w_dd = weapon.getDefaultDatabase(weaponStack).getOgnaWeaponData().getDmgDataSet();
            DmgDataSet s_dd = skill == null ? null : skill.getSkillData().getDmgDataSet();
            this.dmgDataSet = s_dd == null ? w_dd.getDamageData(DDunitID) :
                (w_dd.hasDamageData(DDunitID) && s_dd.hasDamageData(DDunitID)) ?
                    DmgDataSet.DamageData.combine(s_dd.getDamageData(DDunitID),w_dd.getDamageData(DDunitID)) :
                    s_dd.hasDamageData(DDunitID) ? s_dd.getDamageData(DDunitID) : w_dd.getDamageData(DDunitID);
            return this;
        }

        public Builder baseVal(double baseVal)
        {
            this.baseVal = baseVal;
            return this;
        }

        public Builder baseFilter(Predicate<LivingEntity> baseFilter)
        {
            this.baseFilter = baseFilter;
            return this;
        }

        public Builder moveUnit(MoveUnit moveUnit)
        {
            this.moveUnit = moveUnit;
            return this;
        }

        public CastContext build()
        {
            if (attackEntityUnit == null || serverLevel == null || caster == null ||
                weapon == null || weaponCap == null || weaponStack == null ||
                dmgDataSet == null || moveUnit == null)
                throw new IllegalStateException("CastContext fields must not be null");
            return new CastContext(
                attackEntityUnit,
                serverLevel,caster,isPlayer,
                weapon,weaponCap,weaponStack,
                dmgDataSet,baseVal,baseFilter,
                moveUnit);
        }
    }

}
