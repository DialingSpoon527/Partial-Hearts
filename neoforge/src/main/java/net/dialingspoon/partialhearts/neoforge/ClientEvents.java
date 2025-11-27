package net.dialingspoon.partialhearts.neoforge;

import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ClientEvents {
    @EventBusSubscriber(modid = PartialHearts.MOD_ID, value = Dist.CLIENT)
    public static class ClientNeoForgeEvents {
        @SubscribeEvent
        public static void onKey(InputEvent.Key event) {
            if (ModKeys.PARTIALHEARTS_MENU.consumeClick()) {
                Minecraft.getInstance().setScreen(new PatternListScreen(null));
            }
        }

        @SubscribeEvent
        public static void initKeys(RegisterKeyMappingsEvent event) {
            event.register(ModKeys.PARTIALHEARTS_MENU);
        }

        @SubscribeEvent
        public static void registerClientReloadListener(AddClientReloadListenersEvent event) {
            event.addListener(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "reload_listener"), new ResourceReloadListener());
        }
    }
}
