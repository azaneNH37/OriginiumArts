package com.azane.ogna.item.skill;

import com.azane.ogna.client.lib.IExtraModel;
import com.azane.ogna.client.renderer.ExtraModelItemRenderer;
import com.azane.ogna.client.renderer.OgnaWeaponRenderer;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.OgnaStaff;
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

import java.awt.*;
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

    public static boolean isSkill(ItemStack stack)
    {
        return stack.getItem() instanceof OgnaSkill;
    }

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

    @Override
    public String getDescriptionId(ItemStack pStack)
    {
        return getDataBaseForStack(pStack).getDisplayContext().getName();
    }

    public static ResourceLocation getSkillId(ItemStack stack)
    {
        if(!isSkill(stack)) return null;
        OgnaSkill skillItem = (OgnaSkill) stack.getItem();
        ISkill skill = skillItem.getDataBaseForStack(stack);
        return skill.getId();
    }

    public static ItemStack buildSkillStack(ResourceLocation skillId)
    {
        ISkill skill = CommonDataService.get().getSkill(skillId);
        if(skill == null) return ItemStack.EMPTY;
        return skill.buildItemStack(1);
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
