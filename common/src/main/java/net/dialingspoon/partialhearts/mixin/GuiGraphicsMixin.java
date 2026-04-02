package net.dialingspoon.partialhearts.mixin;

import net.dialingspoon.partialhearts.PatternManager;
import net.dialingspoon.partialhearts.rendering.HeartMaskRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin {
    @ModifyArg(method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;addGuiElement(Lnet/minecraft/client/renderer/state/gui/GuiElementRenderState;)V"))
    private GuiElementRenderState swapRenderState(GuiElementRenderState guiElementRenderState) {
        if (PatternManager.health != -1) {
            guiElementRenderState = new HeartMaskRenderState((BlitRenderState) guiElementRenderState, PatternManager.health);
            PatternManager.health = -1;
        }
        return guiElementRenderState;
    }
}
