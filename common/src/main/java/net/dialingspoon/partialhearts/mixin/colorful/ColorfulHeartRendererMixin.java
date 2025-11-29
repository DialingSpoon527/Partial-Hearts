package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.render.HeartRenderer;

@Mixin(HeartRenderer.class)
public abstract class ColorfulHeartRendererMixin {

    @WrapOperation(method = "renderPlayerHearts", at = @At(value = "INVOKE", target = "Lterrails/colorfulhearts/api/heart/drawing/Heart;draw(Lnet/minecraft/client/gui/GuiGraphics;IIZZZ)V"))
    private void renderPartialHeart(Heart heartInstance, GuiGraphics guiGraphics, int heartX, int heartY, boolean hardcore, boolean highlightContainer, boolean highlightHeart, Operation<Void> original, @Local(name = "player") Player player,
                                    @Local(name = "currentHealth") int currentHealth, @Local(name = "absorption") int absorption, @Local(name = "healthHearts") int healthHearts, @Local(name = "index") int index) {
        int lastHeart = Mth.ceil((currentHealth % 20) / 2.0) - 1;
        if (lastHeart == -1 && healthHearts > 0) lastHeart = 9;

        int lastAbsorptionHeart = -1;
        if (absorption != 0) {
            lastAbsorptionHeart = Mth.ceil(((absorption / 2.0) % (20 - healthHearts)) + Math.min(healthHearts, 10)) - 1;
            if (lastAbsorptionHeart == 9) lastAbsorptionHeart = 19;
        }

        if (index == lastHeart || index == lastAbsorptionHeart && !heartInstance.isContainer()) {
            if (index == lastHeart) {
                PatternManager.health = player.getHealth();
            } else {
                PatternManager.health = player.getAbsorptionAmount();
            }
        }

        original.call(heartInstance, guiGraphics, heartX, heartY, hardcore, highlightContainer, highlightHeart);
    }
}
