package net.dialingspoon.partialhearts.neoforge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = PartialHearts.MOD_ID, value = Dist.CLIENT)
    public static class ClientNeoForgeEvents {
        @SubscribeEvent
        public static void onKey(InputEvent.Key event) {
            if (ModKeys.PARTIALHEARTS_MENU.consumeClick()) {
                Minecraft.getInstance().setScreen(new PatternListScreen(null));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = PartialHearts.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), new ResourceLocation(PartialHearts.MOD_ID, "heart_mask"), DefaultVertexFormat.POSITION_TEX),
                    shader -> PartialHearts.SHADER = shader
            );
        }

        @SubscribeEvent
        public static void initKeys(RegisterKeyMappingsEvent event) {
            event.register(ModKeys.PARTIALHEARTS_MENU);
        }

        @SubscribeEvent
        public static void registerClientReloadListener(RegisterClientReloadListenersEvent event) {
            ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(new ResourceReloadListener());
        }
    }
}
