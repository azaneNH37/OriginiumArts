package com.azane.ogna.client.gui.ldlib.custom;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.craft.RecipeIngredient;
import com.azane.ogna.lib.NumStrHelper;
import com.azane.ogna.lib.RlHelper;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@LDLRegister(name = "material", group = "widget.custom")
public class MaterialWidget extends WidgetGroup
{
    public final static ResourceTexture MTR_BACK_TEXTURE = new ResourceTexture(RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/craft/mtr_back.png"));
    public final static ResourceBorderTexture MTR_AMT_TEXTURE = new ResourceBorderTexture(RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/craft/mtr_amt.png").toString(),32,32,3,3);

    @Configurable(name = "ldlib.gui.editor.name.slot_background")
    @Setter
    private IGuiTexture itemTexture;

    public MaterialWidget()
    {
        super(0,0,24,24);
        //addWidget(new ImageWidget(0,0,64,64,MTR_BACK_TEXTURE.copy()).setId("back"));
        addWidget(new ImageWidget(4,3,16,16,()->itemTexture).setId("item"));
        addWidget(new ImageWidget(9,18,21,8,MTR_AMT_TEXTURE.copy()).setId("amt_bg"));
        var text = new TextTextureWidget(0,17,43,10);
        text.setId("amt_text");
        text.getTextTexture().scale(0.6f);
        text.getTextTexture().setRollSpeed(0.3f);
        text.getTextTexture().setType(TextTexture.TextType.ROLL);
        addWidget(text);
    }

    @Override
    public void initTemplate()
    {
        super.initTemplate();
        this.setBackground(MTR_BACK_TEXTURE.copy());
    }

    @Override
    public void initWidget()
    {
        super.initWidget();
        this.setBackground(MTR_BACK_TEXTURE.copy());
    }

    public boolean injectIngredient(RecipeIngredient ingredient,int exist)
    {
        itemTexture = new ItemStackTexture(ingredient.getIngredient().getItems());
        var itemWidget = (ImageWidget)getFirstWidgetById("item");
        itemWidget.setHoverTooltips(ingredient.getIngredient().getItems()[0].getHoverName());
        var amt = (TextTextureWidget)getFirstWidgetById("amt_text");
        if(amt != null)
        {
            //TODO: Issue on component display
            amt.setText(NumStrHelper.format(exist)+"/"+NumStrHelper.format(ingredient.getCount()));
            amt.getTextTexture().setColor(exist >= ingredient.getCount() ? 0xFF00FF00 : 0xFFFF0000);
        }
        return true;
    }
}
