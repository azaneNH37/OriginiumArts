package com.azane.ogna.client.resource;

import com.azane.ogna.client.resource.manager.ModelAdditionManager;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.resource.manager.NamelyDataManager;
import com.azane.ogna.resource.service.IResourceProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public enum ClientAssetsService
{
    INSTANCE;

    private List<PreparableReloadListener> listeners;

    private NamelyDataManager<ModelAdditionManager> modelAddition;

    public void reloadAndRegister(Consumer<PreparableReloadListener> register)
    {
        if (listeners == null)
        {
            listeners = new ArrayList<>();
            modelAddition = register(new NamelyDataManager<>(ModelAdditionManager.class, IResourceProvider.GSON,"ogna/config","model_addition",rl->rl.getPath().equals("model_addition"),true,i->{}));
        }
        listeners.forEach(register);
    }

    public Set<Map.Entry<ResourceLocation,ModelAdditionManager>> getAllModelAdditions()
    {
        DebugLogger.log("registered model additions from {} configs....",modelAddition.getAllDataEntries().size());
        return modelAddition.getAllDataEntries();
    }

    private <T extends PreparableReloadListener> T register(T listener)
    {
        listeners.add(listener);
        return listener;
    }
}