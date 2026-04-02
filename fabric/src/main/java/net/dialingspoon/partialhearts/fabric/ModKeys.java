package net.dialingspoon.partialhearts.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ModKeys {
    public static final KeyMapping.Category KEY_CATEGORY_SPEEDCAP = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("partialhearts", "main"));
    public static KeyMapping PARTIALHEARTS_MENU = new KeyMapping("key.partialhearts.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY_SPEEDCAP);

    public static void register() {
        KeyMappingHelper.registerKeyMapping(PARTIALHEARTS_MENU);
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (ModKeys.PARTIALHEARTS_MENU.consumeClick()) {
                Minecraft.getInstance().setScreen(new PatternListScreen(null));
            }
        });
    }
}
