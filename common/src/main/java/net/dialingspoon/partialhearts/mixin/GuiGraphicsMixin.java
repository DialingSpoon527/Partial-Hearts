package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.partialhearts.PatternManager;
import net.dialingspoon.partialhearts.rendering.HeartMaskRenderType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Function;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @ModifyVariable(method = "innerBlit", at = @At(value = "HEAD"))
    private Function<ResourceLocation, RenderType> changeShader(Function<ResourceLocation, RenderType> value, @Local(ordinal = 0, argsOnly = true) float f, @Local(ordinal = 1, argsOnly = true) float g, @Local(ordinal = 2, argsOnly = true) float h, @Local(ordinal = 3, argsOnly = true) float n) {
        if (PatternManager.health != -1) {
            PatternManager.prepareShader(PatternManager.health, f, g, h, n);
            value = HeartMaskRenderType::heartMask;
            PatternManager.health = -1;
        }
        return value;
    }
}
