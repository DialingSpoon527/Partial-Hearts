package net.dialingspoon.partialhearts.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformSpecificImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
