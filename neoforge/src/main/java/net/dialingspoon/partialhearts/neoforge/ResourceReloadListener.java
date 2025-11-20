package net.dialingspoon.partialhearts.neoforge;

import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        PatternManager.onResourceManagerReload();
    }
}
