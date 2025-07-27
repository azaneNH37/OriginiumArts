package com.azane.ogna.client.gui.ldlib.custom;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;

@LDLRegister(name = "material.group", group = "widget.custom")
public class MaterialWidgetGroup extends DraggableScrollableWidgetGroup
{
    @Configurable(name = "material.amt")
    @NumberRange(range = {0,100})
    private int materialAmt = 0;

    public MaterialWidgetGroup()
    {
        super(0,0,220,56);
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
            addWidget(new MaterialWidget());
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
        this.setXScrollBarHeight(3);
        this.setYScrollBarWidth(0);
        this.setXBarStyle(new ColorRectTexture(0xFF000000),new ColorRectTexture(0xFF006600));
        this.setScrollWheelDirection(ScrollWheelDirection.HORIZONTAL);
    }

    @Override
    public void computeMax()
    {
        super.computeMax();
        isComputingMax = true;
        maxWidth += getLayoutPadding()*4;
        isComputingMax = false;
    }
}
