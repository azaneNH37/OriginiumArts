package com.azane.ogna.client.gui.ldlib.custom;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.client.lib.RegexHelper;
import com.azane.ogna.craft.RlResultRecipe;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.resource.service.CommonDataService;
import com.azane.ogna.util.RlrRecipes;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

import static com.azane.ogna.client.lib.RegexHelper.endWith;

@LDLRegister(name = "menu.item",group = "widget.custom")
public class MenuItemWidget extends WidgetGroup
{
    public static final ResourceBorderTexture MENU_BG = new ResourceBorderTexture(RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/craft/menu_unit_bg.png").toString(),32,32,3,3);
    public static final ResourceBorderTexture WP_TYPE_BG = new ResourceBorderTexture(RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/craft/wp_type_bg.png").toString(),32,32,1,1);
    public static final ColorRectTexture BUTTON_HOVER = new ColorRectTexture(0x22FFFFFF);

    @Configurable(name = "item.type")
    @Setter
    @Getter
    private IGuiTexture typeTexture;

    public MenuItemWidget()
    {
        super(0,0,118,22);
        setBackground(MENU_BG.copy());
        var button = new ButtonWidget(3,3,112,16,cd->{});
        button.setId("button");
        button.setHoverTexture(BUTTON_HOVER.copy());
        button.setClickedTexture(BUTTON_HOVER.copy());
        addWidget(button);
        var slotWidget = new SlotWidget().setId("item");
        slotWidget.setSelfPosition(4,2);
        addWidget(slotWidget);
        addWidget(new ImageWidget(24,4,14,14,WP_TYPE_BG.copy()).setId("wp_type_bg"));
        addWidget(new ImageWidget(26,6,10,10,()->typeTexture).setId("wp_type_icon"));
        var text1 = new TextTextureWidget(42,5,70,12);
        text1.setId("name");
        text1.getTextTexture().setType(TextTexture.TextType.LEFT_ROLL);
        text1.getTextTexture().setRollSpeed(0.4f);
        addWidget(text1);
        var text2 = new TextTextureWidget(44,15,85,5);
        text2.setId("code_name");
        text2.getTextTexture().scale(0.6f);
        text2.getTextTexture().setType(TextTexture.TextType.RIGHT);
        addWidget(text2);
    }

    @Override
    public void initTemplate()
    {
        super.initTemplate();
        setBackground(MENU_BG.copy());
    }

    @Override
    public void initWidget()
    {
        super.initWidget();
        for(var widget : widgets)
        {
            if(widget instanceof SlotWidget slotWidget)
            {
                slotWidget.setCanPutItems(false);
                slotWidget.setCanTakeItems(false);
            }
        }
    }

    public boolean injectRecipe(RlResultRecipe recipe, Container displayContainer,int index)
    {
        String type = recipe.getResult().getType();
        ResourceLocation resrl = recipe.getResult().getId();
        var func = RlrRecipes.map.get(type);
        if(func == null || !func.getExistence().test(resrl))
            return false;
        var slot = UiHelper.getAsNonnull(SlotWidget.class, RegexHelper.startWith("item"),this.widgets);
        var name = UiHelper.getAsNonnull(TextTextureWidget.class, RegexHelper.startWith("name"), this.widgets);
        var code = UiHelper.getAsNonnull(TextTextureWidget.class, RegexHelper.startWith("code_name"), this.widgets);
        if (type.equals("staff"))
        {
            var data = Objects.requireNonNull(CommonDataService.get().getStaff(resrl)) ;
            typeTexture = new ResourceTexture(data.getDisplayContext().getTypeIcon());
            name.setText(data.getDisplayContext().getName());
            name.getTextTexture().setColor(data.getDisplayContext().getColor());
            code.setText(data.getDisplayContext().getCodeName());
            displayContainer.setItem(index,data.buildItemStack(1));
            slot.setContainerSlot(displayContainer,index);
        }
        return true;
    }

    //TODO: 翻页按钮一定要清空展示槽位的container，否则会导致显示错误
    public void displayInMainUI(List<Widget> displayList, RlResultRecipe recipe,Container displayContainer,int index)
    {
        var modelView = UiHelper.getAsNonnull(TriDImageWidget.class,endWith("model"),displayList);
        var slot = UiHelper.getAsNonnull(SlotWidget.class, endWith("slot"), displayList);
        var typeIcon = UiHelper.getAsNonnull(ImageWidget.class, endWith("type"), displayList);
        var name = UiHelper.getAsNonnull(TextTextureWidget.class, endWith("name"), displayList);

        slot.setContainerSlot(displayContainer,index);
        modelView.setItemSupplier(slot::getItem);
        typeIcon.setImage(getTypeTexture());
        var menu_name = UiHelper.getAsNonnull(TextTextureWidget.class, RegexHelper.startWith("name"), this.widgets);
        name.setText(menu_name.getLastComponent());
        name.getTextTexture().setColor(menu_name.getTextTexture().color);
    }
}