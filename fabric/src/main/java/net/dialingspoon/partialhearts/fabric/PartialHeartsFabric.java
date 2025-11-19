package net.dialingspoon.partialhearts.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.IOException;

public final class PartialHeartsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListener());
        CoreShaderRegistrationCallback.EVENT.register(PartialHeartsFabric::onShaderReload);
        ModKeys.register();
    }

    public static void onShaderReload(CoreShaderRegistrationCallback.RegistrationContext context) throws IOException {
        context.register(new ResourceLocation("partialhearts", "heart_mask"), DefaultVertexFormat.POSITION_TEX, shader -> PartialHearts.SHADER = shader);
    }
}
