package com.azane.ogna.client.gui.ldlib.helper;

import com.azane.ogna.lib.RlHelper;
import com.lowdragmc.lowdraglib.gui.editor.data.UIProject;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//TODO:需要C/S隔离测试
@ParametersAreNonnullByDefault
public class UiHelper
{
    private static final Map<ResourceLocation,Supplier<WidgetGroup>> CACHE = new HashMap<>();
    public static final String UI_PATH = "ldl_ui/";

    private UiHelper(){}

    public static int clearCache() {
        int count = CACHE.size();
        CACHE.clear();
        return count;
    }

    @Nullable
    public static Supplier<WidgetGroup> getUISupplier(ResourceLocation uiLocation,boolean isClient)
    {
        return CACHE.computeIfAbsent(uiLocation,id->{
            ResourceLocation rl = RlHelper.build(id.getNamespace(),UI_PATH+id.getPath()+".ui");
            ResourceManager resourceManager = isClient ? Minecraft.getInstance().getResourceManager() : ServerLifecycleHooks.getCurrentServer().getResourceManager();
            try (InputStream inputStream = resourceManager.open(rl)) {
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                CompoundTag tag = NbtIo.read(dataInputStream);
                return UIProject.loadUIFromTag(tag);
            } catch (Exception var10) {
                return null;
            }
        });
    }
}
