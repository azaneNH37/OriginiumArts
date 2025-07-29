package com.azane.ogna.item.skill;

import com.azane.ogna.client.lib.IExtraModel;
import com.azane.ogna.client.renderer.ExtraModelItemRenderer;
import com.azane.ogna.client.renderer.OgnaWeaponRenderer;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.OgnaStaff;
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

public class OgnaSkill extends Item implements IGenItem, IPolyItemDataBase<ISkill>, IExtraModel
{
    @Getter
    private final Class<ISkill> dataBaseType = ISkill.class;
    @Getter
    private final Map<ResourceLocation,ISkill> databaseCache = new ConcurrentHashMap<>();

    public OgnaSkill() {super(new Properties().stacksTo(1));}

    @Override
    @SuppressWarnings("unchecked")
    public OgnaSkill getItem() {return this;}

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

    public static NonNullList<ItemStack> fillCreativeTab() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ServerDataService.get().getAllSkills().stream()
            .sorted(Comparator.comparing(e->e.getKey().toString()))
            .forEach(entry->{
            stacks.add(entry.getValue().buildItemStack(1));
        });
        return stacks;
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
}
