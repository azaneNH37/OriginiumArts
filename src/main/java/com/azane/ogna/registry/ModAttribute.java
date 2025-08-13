package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author azaneNH37 (2025-08-11)
 */
public class ModAttribute
{
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, OriginiumArts.MOD_ID);

    public static final RegistryObject<Attribute> WEAPON_ATTACK_CD = registerRange("weapon.attack.cd",1500,50,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> WEAPON_RELOAD_CD = registerRange("weapon.reload.cd",3000,50,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> WEAPON_MAX_CHARGE_TIME = registerRange("weapon.charge.max",3000,50,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> WEAPON_ENERGY_STORE = registerRange("weapon.energy.store",100,0,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> WEAPON_ENERGY_CONSUME = registerRange("weapon.energy.consume",10,Double.MIN_VALUE,Double.MAX_VALUE);

    public static final RegistryObject<Attribute> SKILL_SP = registerRange("skill.sp",300,1,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> SKILL_DURATION = registerRange("skill.duration",150,1,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> SKILL_SP_RATE = registerRange("skill.sp.rate",1,1-Double.MAX_VALUE,Double.MAX_VALUE);

    public static final RegistryObject<Attribute> CHIP_SET_VOLUME = registerRange("chip.set.volume",100,0,Double.MAX_VALUE);

    public static final RegistryObject<Attribute> EFFECT_LEVEL = registerRange("effect.level",0,1-Double.MAX_VALUE,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> EFFECT_TICK = registerRange("effect.tick",0,1-Double.MAX_VALUE,Double.MAX_VALUE);

    public static final RegistryObject<Attribute> DAMAGE_PHYSICS = registerRange("damage.physics",0,1-Double.MAX_VALUE,Double.MAX_VALUE);
    public static final RegistryObject<Attribute> DAMAGE_ARTS = registerRange("damage.arts",0,1-Double.MAX_VALUE,Double.MAX_VALUE);

    public static RegistryObject<Attribute> registerRange(String name,double defaultVal,double minVal,double maxVal)
    {
        return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute.name." +OriginiumArts.MOD_ID+"."+ name, defaultVal, minVal, maxVal));
    }
}
