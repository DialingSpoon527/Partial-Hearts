package net.dialingspoon.partialhearts.forge;

import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ResourceReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        PatternManager.onResourceManagerReload(resourceManager);
    }
}
