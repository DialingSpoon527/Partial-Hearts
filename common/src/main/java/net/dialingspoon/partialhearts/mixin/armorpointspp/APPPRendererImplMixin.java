package net.dialingspoon.partialhearts.mixin.armorpointspp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cheos.armorpointspp.impl.RendererImpl;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(RendererImpl.class)
public abstract class APPPRendererImplMixin {
    @WrapOperation(method = "blitM(Ldev/cheos/armorpointspp/core/adapter/IPoseStack;IIFFIIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), remap = false)
    private static void changeShader(Supplier<ShaderInstance> supplier, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) float u, @Local(ordinal = 1, argsOnly = true) float v, @Local(ordinal = 2, argsOnly = true) int width, @Local(ordinal = 3, argsOnly = true) int height, @Local(ordinal = 4, argsOnly = true) int texWidth, @Local(ordinal = 5, argsOnly = true) int texHeight) {
        if (PatternManager.health != -1) {
            supplier = PatternManager.getShader(PatternManager.health, u / texWidth, v / texHeight, (u + width) / texWidth, (v + height) / texHeight);
            PatternManager.health = -1;
        }
        original.call(supplier);
    }
}
