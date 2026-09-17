package net.dialingspoon.partialhearts.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ModKeys {
    public static final String KEY_CATEGORY_SPEEDCAP = "key.category.partialhearts";
    public static KeyMapping PARTIALHEARTS_MENU = new KeyMapping("key.partialhearts.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY_SPEEDCAP);

    public static void register() {
        KeyBindingHelper.registerKeyBinding(PARTIALHEARTS_MENU);
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (ModKeys.PARTIALHEARTS_MENU.consumeClick()) {
                Minecraft.getInstance().setScreen(new PatternListScreen(null));
            }
        });
    }
}
