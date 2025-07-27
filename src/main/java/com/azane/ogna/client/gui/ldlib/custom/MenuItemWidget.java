package com.azane.ogna.client.gui.ldlib.custom;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import lombok.Setter;

@LDLRegister(name = "menu.item",group = "widget.custom")
public class MenuItemWidget extends WidgetGroup
{
    @Configurable(name = "item.type")
    @Setter
    private IGuiTexture typeTexture;

    public MenuItemWidget()
    {
        super(0,0,128,24);
        addWidget(new ButtonWidget(3,2,122,20,this::onClick).setId("button"));
        var slotWidget = new SlotWidget().setId("item");
        slotWidget.setSelfPosition(2,3);
        addWidget(slotWidget);
        addWidget(new ImageWidget(23,3,18,18,()->typeTexture).setId("type"));
        var text1 = new TextTextureWidget(45,5,80,16);
        text1.setId("name");
        text1.getTextTexture().setType(TextTexture.TextType.LEFT_ROLL);
        text1.getTextTexture().setRollSpeed(0.4f);
        addWidget(text1);
        var text2 = new TextTextureWidget(80,18,60,6);
        text2.setId("code_name");
        text2.getTextTexture().scale(0.6f);
        text2.getTextTexture().setType(TextTexture.TextType.RIGHT);
        addWidget(text2);
    }

    public void onClick(ClickData data)
    {

    }
    @Override
    public void initWidget()
    {
        super.initWidget();
        for(var widget : widgets)
        {
            if(!widget.getId().equals("type"))
                widget.setBackground((IGuiTexture) null);
            if(widget instanceof SlotWidget slotWidget)
            {
                slotWidget.setCanPutItems(false);
                slotWidget.setCanTakeItems(false);
            }
        }
    }
}