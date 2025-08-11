package com.azane.ogna.item.geoblock;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.util.GeoAnimations;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

//TODO:为什么！不写！wiki！
/**
 * @author azaneNH37 (2025-08-11)
 */
public class CraftOCCBlockItem extends BlockItem implements GeoItem
{
    //===== GeckoLib start ======
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"misc", state -> state.setAndContinue(GeoAnimations.MISC_WORK)));
    }
    //===== GeckoLib end =======

    public CraftOCCBlockItem(Block pBlock, Properties pProperties)
    {
        super(pBlock, pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag)
    {
        pTooltip.add(Component.translatable("block.ognmarts.craft_occ.desp").withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<CraftOCCBlockItem> renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(RlHelper.build(OriginiumArts.MOD_ID, "craft_occ")));
                    this.renderer.addRenderLayer(new AutoGlowingGeoLayer<>(this.renderer));
                    //TODO:小心AutoGlowingGeoLayer，有glowmask不加glowing会出现神秘渲染问题（错题本++
                }
                return this.renderer;
            }
        });
    }
}
