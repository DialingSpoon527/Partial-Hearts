package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.api.heart.drawing.SpriteHeartDrawing;

@Mixin(value = SpriteHeartDrawing.class, remap = false)
public abstract class ColorfulSpriteHeartDrawingMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 4, argsOnly = true)
    private static ResourceLocation notHalf(ResourceLocation resourceLocation, @Local(ordinal = 1, argsOnly = true) ResourceLocation full) {
        return full;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 5, argsOnly = true)
    private static ResourceLocation notHalfBlinking(ResourceLocation resourceLocation, @Local(ordinal = 2, argsOnly = true) ResourceLocation fullBlinking) {
        return fullBlinking;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 8, argsOnly = true)
    private static ResourceLocation notHardcoreHalf(ResourceLocation resourceLocation, @Local(ordinal = 5, argsOnly = true) ResourceLocation hardcoreFull) {
        return hardcoreFull;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 9, argsOnly = true)
    private static ResourceLocation notHardcoreHalfBlinking(ResourceLocation resourceLocation, @Local(ordinal = 6, argsOnly = true) ResourceLocation hardcoreFullBlinking) {
        return hardcoreFullBlinking;
    }
}
