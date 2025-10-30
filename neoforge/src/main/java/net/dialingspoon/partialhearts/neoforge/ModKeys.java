package net.dialingspoon.partialhearts.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class ModKeys {
    public static final KeyMapping.Category KEY_CATEGORY_SPEEDCAP = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("partialhearts", "main"));
    public static KeyMapping PARTIALHEARTS_MENU = new KeyMapping("key.partialhearts.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY_SPEEDCAP);
}
