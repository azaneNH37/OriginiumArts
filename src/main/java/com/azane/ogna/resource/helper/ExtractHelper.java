package com.azane.ogna.resource.helper;

import net.minecraft.resources.ResourceLocation;

public final class ExtractHelper
{
    public static String extractPureName(ResourceLocation id)
    {
        String path = id.getPath();
        int slashIndex = path.lastIndexOf('/');
        return (slashIndex == -1) ? path : path.substring(slashIndex + 1);
    }

    public static String extractTypePrefix(ResourceLocation id)
    {
        String filename = extractPureName(id);
        int underscoreIndex = filename.indexOf('-');
        if (underscoreIndex == -1) {
            return filename;
        }
        return filename.substring(0, underscoreIndex);
    }

    public static ResourceLocation extractPureId(ResourceLocation id)
    {
        String path = id.getPath();
        int slashIndex = path.lastIndexOf('/');
        return ResourceLocation.tryBuild(id.getNamespace(), (slashIndex == -1) ? path : path.substring(slashIndex + 1));
    }
}
