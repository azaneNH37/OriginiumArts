package com.azane.ogna.fluid;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlock;
import com.azane.ogna.registry.ModFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Consumer;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class OriginiumEnergyFluid extends FluidType
{
    private static final ResourceLocation STILL_TEXTURE = RlHelper.build(OriginiumArts.MOD_ID,"block/liquid");
    private static final ResourceLocation FLOWING_TEXTURE = RlHelper.build(OriginiumArts.MOD_ID,"block/liquid_flow");

    public static final String NAME = "originium_energy";

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
        ModFluid.ORIGINIUM_ENERGY,
        ModFluid.SOURCE_ORIGINIUM_ENERGY,
        ModFluid.FLOWING_ORIGINIUM_ENERGY
    ).block(()-> (LiquidBlock) ModBlock.OE_FLUID.get());


    public OriginiumEnergyFluid()
    {
        super(Properties.create()
            .descriptionId("fluid.ognmarts.%s".formatted(NAME))
            .lightLevel(8)
            .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions()
        {
            @Override
            public ResourceLocation getFlowingTexture() {return FLOWING_TEXTURE;}
            @Override
            public ResourceLocation getStillTexture() {return STILL_TEXTURE;}
            @Override
            public int getTintColor() {return 0xFFFFCB00;}
        });
    }
}
