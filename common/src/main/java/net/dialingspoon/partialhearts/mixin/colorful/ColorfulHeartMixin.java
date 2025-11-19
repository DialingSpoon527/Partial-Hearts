package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.api.heart.drawing.HeartDrawing;

@Mixin(Heart.class)
public abstract class ColorfulHeartMixin {
    @Unique
    float health;

    @Inject(method = "draw", at = @At(value = "HEAD"))
    private void storeHealthLocal(GuiGraphics guiGraphics, int x, int y, boolean hardcore, boolean highlightContainer, boolean highlightHeart, CallbackInfo ci) {
        health = PatternManager.health;
        PatternManager.health = -1;
    }

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lterrails/colorfulhearts/api/heart/drawing/HeartDrawing;draw(Lnet/minecraft/client/gui/GuiGraphics;IIZZZ)V"))
    private void reinstateHealth(GuiGraphics guiGraphics, int x, int y, boolean hardcore, boolean highlightContainer, boolean highlightHeart, CallbackInfo ci) {
        PatternManager.health = health;
        health = -1;
    }

    @WrapMethod(method = "full(Lterrails/colorfulhearts/api/heart/drawing/HeartDrawing;ZLterrails/colorfulhearts/api/heart/drawing/Heart;)Lterrails/colorfulhearts/api/heart/drawing/Heart;", remap = false)
    private static Heart makeFull(HeartDrawing drawing, boolean half, Heart background, Operation<Heart> original) {
        return original.call(drawing, false, background);
    }
}
