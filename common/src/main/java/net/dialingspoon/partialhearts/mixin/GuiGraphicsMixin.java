package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @WrapOperation(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"))
    private void changeShader(Supplier<ShaderInstance> supplier, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) float f, @Local(ordinal = 1, argsOnly = true) float g, @Local(ordinal = 2, argsOnly = true) float h, @Local(ordinal = 3, argsOnly = true) float n) {
        if (PatternManager.health != -1) {
            supplier = PatternManager.getShader(PatternManager.health, f, g, h, n);
            PatternManager.health = -1;
        }
        original.call(supplier);
    }
}
