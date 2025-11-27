package net.dialingspoon.partialhearts.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class PlatformSpecificImpl {
    public static boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        if (modList != null)
            return modList.isLoaded(modId);
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
    }
}
