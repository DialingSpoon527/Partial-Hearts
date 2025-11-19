package net.dialingspoon.partialhearts.forge.mixin;

import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin {
    @Inject(method = "renderHealth", at = @At(value = "FIELD", target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;displayHealth:I", opcode = Opcodes.PUTFIELD))
    private void floatDisplayHealthUpdate(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        PatternManager.displayHealthFloat = ((Player) Minecraft.getInstance().getCameraEntity()).getHealth();
    }
}
