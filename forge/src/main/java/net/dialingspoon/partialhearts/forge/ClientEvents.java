package net.dialingspoon.partialhearts.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

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
                    new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "heart_mask"), DefaultVertexFormat.POSITION_TEX),
                    shader -> PartialHearts.SHADER = shader
            );
            if (ModList.get().isLoaded("colorfulhearts"))
                event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "heart_mask_color"), DefaultVertexFormat.POSITION_COLOR_TEX),
                    shader -> PartialHearts.COLOR_SHADER = shader
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
