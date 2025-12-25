package net.dialingspoon.partialhearts.fabric;

import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.PatternManager;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadListener implements SimpleResourceReloadListener<Void> {
    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(PartialHearts.MOD_ID, "reload_listener");
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager resourceManager, Executor executor) {
        PatternManager.onResourceManagerReload();
        return CompletableFuture.completedFuture(null);
    }
}
