package com.azane.ogna.lib;

import com.azane.ogna.OriginiumArts;
import net.minecraft.resources.ResourceLocation;

/**
 * What the holy f**king shit does Forge want f**king to do in single version 1.20.1 ????
 * @author azaneNH37 (2025-07-13)
 */
public final class RlHelper
{
    public static final ResourceLocation EMPTY = RlHelper.build(OriginiumArts.MOD_ID,"null");

    public static ResourceLocation parse(String rl)
    {
        return ResourceLocation.tryParse(rl);
    }

    public static ResourceLocation build(String namespace,String path)
    {
        return ResourceLocation.tryBuild(namespace,path);
    }
}
