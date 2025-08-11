package com.azane.ogna.genable.data.display;

import com.azane.ogna.genable.data.TriDDisplayData;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * @author azaneNH37 (2025-08-10)
 */
public interface IDisplayContext
{
    IDisplayContext EMPTY = new IDisplayContext()
    {
        @Override
        public String getName() { return "ogna.genable.missing.name"; }
        @Override
        public String getDescription() { return ""; }
        @Override
        public String getCodeName() { return ""; }
        @Override
        public int getColor() { return 0xFFFFFFFF; }
        @Override
        public ResourceLocation getTypeIcon() { return RlHelper.EMPTY; }
        @Override
        public TriDDisplayData getTriDDisplayData() { return new TriDDisplayData(); }
    };

    String getName();
    String getDescription();
    String getCodeName();
    int getColor();
    ResourceLocation getTypeIcon();
    TriDDisplayData getTriDDisplayData();
}
