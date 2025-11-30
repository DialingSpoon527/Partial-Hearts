package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Nullable protected abstract Player getCameraPlayer();
    @Unique
    private boolean partialhearts$first = true;
    @Unique
    private boolean partialhearts$aborptionFirst = true;
    @Unique
    private boolean partialhearts$blinkingCalled = false;

    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V", ordinal = 1))
    private void prepareAbsorptionMask(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, boolean hardcore, boolean blinking, boolean half, Operation<Void> original) {
        if (partialhearts$aborptionFirst) {
            partialhearts$aborptionFirst = false;
            PatternManager.health = getCameraPlayer().getAbsorptionAmount();
        }
        original.call(instance, guiGraphics, heartType, heartX, heartY, hardcore, blinking, false);
    }
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V", ordinal = 2))
    private void prepareFlashingMask(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, boolean hardcore, boolean blinking, boolean half, Operation<Void> original) {
        if (partialhearts$first) {
            partialhearts$first = false;
            partialhearts$blinkingCalled = true;
            PatternManager.health = PatternManager.displayHealthFloat;
        }
        original.call(instance, guiGraphics, heartType, heartX, heartY, hardcore, blinking, false);
    }
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V", ordinal = 3))
    private void prepareHeartMask(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, boolean hardcore, boolean blinking, boolean half, Operation<Void> original) {
        if (!partialhearts$blinkingCalled) {
            if (partialhearts$first) {
                partialhearts$first = false;
                PatternManager.health = getCameraPlayer().getHealth();
            }
            original.call(instance, guiGraphics, heartType, heartX, heartY, hardcore, blinking, false);
        }
    }

    @Inject(method = "renderHearts", at = @At("TAIL"))
    public void resetFirstHeartTrackers(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
        partialhearts$first = true;
        partialhearts$aborptionFirst = true;
        partialhearts$blinkingCalled = false;
    }
}
