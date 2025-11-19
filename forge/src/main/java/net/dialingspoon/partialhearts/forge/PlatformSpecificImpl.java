package net.dialingspoon.partialhearts.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class PlatformSpecificImpl {
    public static boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        if (modList != null)
            return modList.isLoaded(modId);
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}
