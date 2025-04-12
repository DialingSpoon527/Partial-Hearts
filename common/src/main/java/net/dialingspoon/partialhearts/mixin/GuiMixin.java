package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.dialingspoon.partialhearts.PatternManager;
import net.dialingspoon.partialhearts.interfaces.IGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements IGui {
    @Shadow @Final private static ResourceLocation GUI_ICONS_LOCATION;
    @Unique
    private boolean partialhearts$first = true;
    @Unique
    private boolean partialhearts$aborptionFirst = true;
    @Unique
    private boolean partialhearts$blinkingCalled = false;
    @Unique
    public float partialhearts$displayHealthFloat;
    @Unique
    public void setdisplayHealthFloat(float value) {
        partialhearts$displayHealthFloat = value;
    }

    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 1))
    private void renderAbsorptionHearts(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, int textureY, boolean blinking, boolean half, Operation<Void> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        float absorptionAmount = player.getAbsorptionAmount();
        if (partialhearts$aborptionFirst) {
            partialhearts$aborptionFirst = false;
            PatternManager.renderHeart(PatternManager.getImage(GUI_ICONS_LOCATION, heartType.getX(player.level().getLevelData().isHardcore(), blinking), textureY), guiGraphics, absorptionAmount, heartX, heartY);
        } else {
            original.call(instance, guiGraphics, heartType, heartX, heartY, textureY, blinking, false);
        }
    }
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 2))
    private void renderFlashingHearts(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, int textureY, boolean blinking, boolean half, Operation<Void> original) {
        float healthAmount = partialhearts$displayHealthFloat;
        if (partialhearts$first) {
            partialhearts$first = false;
            partialhearts$blinkingCalled = true;
            PatternManager.renderHeart(PatternManager.getImage(GUI_ICONS_LOCATION, heartType.getX(Minecraft.getInstance().player.level().getLevelData().isHardcore(), blinking), textureY), guiGraphics, healthAmount, heartX, heartY);
        } else {
            original.call(instance, guiGraphics, heartType, heartX, heartY, textureY, blinking, false);
        }
    }
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 3))
    private void renderHearts(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int heartX, int heartY, int textureY, boolean blinking, boolean half, Operation<Void> original) {
        if (!partialhearts$blinkingCalled) {
            LocalPlayer player = Minecraft.getInstance().player;
            float healthAmount = player.getHealth();

            if (partialhearts$first) {
                partialhearts$first = false;
                PatternManager.renderHeart(PatternManager.getImage(GUI_ICONS_LOCATION, heartType.getX(player.level().getLevelData().isHardcore(), blinking), textureY), guiGraphics, healthAmount, heartX, heartY);
            } else {
                original.call(instance, guiGraphics, heartType, heartX, heartY, textureY, blinking, false);
            }
        }
    }

    @Inject(method = "renderHearts", at = @At("TAIL"))
    public void resetFirstHeart(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
        partialhearts$first = true;
        partialhearts$aborptionFirst = true;
        partialhearts$blinkingCalled = false;
    }
}
