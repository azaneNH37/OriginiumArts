package com.azane.ogna.client.gui.ldlib.custom;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.lib.RlHelper;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import lombok.Getter;

/**
 * @author azaneNH37 (2025-07-29)
 */
@LDLRegister(name = "material.group", group = "widget.custom")
public class MaterialWidgetGroup extends DraggableScrollableWidgetGroup
{
    public static final ResourceBorderTexture SLIDE_4 = new ResourceBorderTexture(RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/craft/slide_4.png").toString(),32,32,2,2);
    public static final String CHILD_ID = "mtr.unit";

    @Configurable(name = "material.amt")
    @NumberRange(range = {0,100})
    @Getter
    private int materialAmt = 0;

    public MaterialWidgetGroup()
    {
        super(0,0,120,28);
    }

    @ConfigSetter(field = "materialAmt")
    public void setMaterialAmt(int materialAmt)
    {
        if(materialAmt == this.materialAmt)
            return;
        while (this.materialAmt > materialAmt)
        {
            removeWidget(widgets.get(widgets.size()-1));
            this.materialAmt--;
        }
        while (this.materialAmt < materialAmt)
        {
            addWidget(new MaterialWidget().setId(CHILD_ID));
            this.materialAmt++;
        }
        this.setLayout(Layout.HORIZONTAL_TOP);
        this.setLayout(Layout.NONE);
    }

    @Override
    public void initTemplate()
    {
        super.initTemplate();
        this.setBackground((IGuiTexture) null);
        //TODO:issue
        this.setLayoutPadding(4);
        this.setXScrollBarHeight(2);
        this.setYScrollBarWidth(0);
        this.setXBarStyle(null,SLIDE_4.copy());
        this.setScrollWheelDirection(ScrollWheelDirection.HORIZONTAL);
    }

    @Override
    public void computeMax()
    {
        super.computeMax();
        isComputingMax = true;
        maxWidth += getLayoutPadding()*3;
        isComputingMax = false;
    }
}
