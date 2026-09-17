package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.api.heart.drawing.SpriteHeartDrawing;

@Mixin(value = SpriteHeartDrawing.class, remap = false)
public abstract class ColorfulSpriteHeartDrawingMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 4, argsOnly = true)
    private static Identifier notHalf(Identifier resourceLocation, @Local(ordinal = 1, argsOnly = true) Identifier full) {
        return full;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 5, argsOnly = true)
    private static Identifier notHalfBlinking(Identifier resourceLocation, @Local(ordinal = 2, argsOnly = true) Identifier fullBlinking) {
        return fullBlinking;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 8, argsOnly = true)
    private static Identifier notHardcoreHalf(Identifier resourceLocation, @Local(ordinal = 5, argsOnly = true) Identifier hardcoreFull) {
        return hardcoreFull;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 9, argsOnly = true)
    private static Identifier notHardcoreHalfBlinking(Identifier resourceLocation, @Local(ordinal = 6, argsOnly = true) Identifier hardcoreFullBlinking) {
        return hardcoreFullBlinking;
    }
}
