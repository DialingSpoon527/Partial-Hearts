package net.dialingspoon.partialhearts.neoforge.mixin;

import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Inject(method = "extractHealthLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;displayHealth:I", opcode = Opcodes.PUTFIELD))
    private void recordDisplayHealth(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        PatternManager.displayHealthFloat = getCameraPlayer().getHealth();
    }
}
