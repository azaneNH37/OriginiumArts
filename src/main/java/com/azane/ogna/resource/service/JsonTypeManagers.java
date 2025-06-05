package com.azane.ogna.resource.service;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.resource.manager.JsonDataTypeManager;
import lombok.Getter;

public class JsonTypeManagers
{
    //此处添加需要建立的jsonTypeManager
    public static final JsonDataTypeManager modTypeManager = new JsonDataTypeManager(OriginiumArts.MOD_ID);

    /**
     *
     * 在{@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}处调用
     */
    public static void loadJsonTypeManagers()
    {
        modTypeManager.initialize();
    }
}
