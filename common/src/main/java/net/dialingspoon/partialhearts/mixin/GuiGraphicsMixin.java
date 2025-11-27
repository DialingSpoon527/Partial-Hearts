package net.dialingspoon.partialhearts.mixin;

import net.dialingspoon.partialhearts.PatternManager;
import net.dialingspoon.partialhearts.rendering.HeartMaskRenderState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @ModifyArg(method = "submitBlit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;submitGuiElement(Lnet/minecraft/client/gui/render/state/GuiElementRenderState;)V"))
    private GuiElementRenderState swapRenderState(GuiElementRenderState guiElementRenderState) {
        if (PatternManager.health != -1) {
            guiElementRenderState = new HeartMaskRenderState((BlitRenderState) guiElementRenderState, PatternManager.health);
            PatternManager.health = -1;
        }
        return guiElementRenderState;
    }
}
