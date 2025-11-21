package net.dialingspoon.partialhearts.fabric;

import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.PatternManager;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadListener implements SimpleResourceReloadListener<Void> {
    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "reload_listener");
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
        PatternManager.onResourceManagerReload();
        return CompletableFuture.completedFuture(null);
    }
}
