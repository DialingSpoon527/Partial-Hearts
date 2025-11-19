package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrails.colorfulhearts.render.RenderUtils;

import java.util.function.Supplier;

@Mixin(value = RenderUtils.class, remap = false)
public class ColorfulRenderUtilsMixin {

    @WrapOperation(method = "drawColoredTexturedQuad", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"))
    private static void changeShader(Supplier<ShaderInstance> supplier, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) float u0, @Local(ordinal = 1, argsOnly = true) float u1, @Local(ordinal = 2, argsOnly = true) float v0, @Local(ordinal = 3, argsOnly = true) float v1, @Local(ordinal = 8, argsOnly = true) int alpha) {
        if (PatternManager.health != -1) {
            supplier = PatternManager.getShader(alpha != 0, PatternManager.health, u0, u1, v0, v1);
            PatternManager.health = -1;
        }
        original.call(supplier);
    }
}

