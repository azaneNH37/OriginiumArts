package com.azane.ogna.item;

import com.azane.ogna.client.lib.IExtraModel;
import com.azane.ogna.client.renderer.ExtraModelItemRenderer;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.resource.service.ServerDataService;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OgnaChip extends Item implements IGenItem, IPolyItemDataBase<IChip>, IExtraModel
{
    @Getter
    private final Class<IChip> dataBaseType = IChip.class;
    @Getter
    private final Map<ResourceLocation,IChip> databaseCache = new ConcurrentHashMap<>();

    public OgnaChip() {super(new Properties().stacksTo(64));}

    @Override
    @SuppressWarnings("unchecked")
    public OgnaChip getItem() {return this;}

    public static boolean isChip(ItemStack stack)
    {
        return stack.getItem() instanceof OgnaChip;
    }

    @Override
    public boolean isDataBaseForStack(ItemStack itemStack)
    {
        return isThisGenItem(itemStack);
    }

    @Override
    public ResourceLocation getGuiModel(ItemStack stack)
    {
        return getDataBaseForStack(stack).getDisplayContext().getModel();
    }

    @Override
    public String getDescriptionId(ItemStack pStack)
    {
        return getDataBaseForStack(pStack).getDisplayContext().getName();
    }

    public static IChip getChip(ResourceLocation rl)
    {
        return CommonDataService.get().getChip(rl);
    }

    public static ResourceLocation getChipId(ItemStack stack)
    {
        if(!isChip(stack)) return null;
        OgnaChip chipItem = (OgnaChip) stack.getItem();
        IChip chip = chipItem.getDataBaseForStack(stack);
        return chip.getId();
    }

    public static ItemStack buildChipStack(ResourceLocation chipId)
    {
        IChip chip = CommonDataService.get().getChip(chipId);
        if(chip == null) return ItemStack.EMPTY;
        return chip.buildItemStack(1);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions()
        {
            private ExtraModelItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                if(renderer == null)
                {
                    renderer = new ExtraModelItemRenderer(Minecraft.getInstance());
                }
                return renderer;
            }
        });
    }

    public static NonNullList<ItemStack> fillCreativeTab() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ServerDataService.get().getAllChips().stream()
            .filter(entry->entry.getValue().isItem())
            .sorted(Comparator.comparing(e->e.getKey().toString()))
            .forEach(entry->{
                stacks.add(entry.getValue().buildItemStack(1));
            });
        return stacks;
    }
}
