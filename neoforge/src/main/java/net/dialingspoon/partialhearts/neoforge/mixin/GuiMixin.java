package net.dialingspoon.partialhearts.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExtendedGui.class)
public abstract class GuiMixin {
    @Inject(method = "renderHealth", at = @At(value = "FIELD", target = "Lnet/neoforged/neoforge/client/gui/overlay/ExtendedGui;displayHealth:I", opcode = Opcodes.PUTFIELD))
    private void recordDisplayHealth(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci, @Local Player player) {
        PatternManager.displayHealthFloat = player.getHealth();
    }
}
