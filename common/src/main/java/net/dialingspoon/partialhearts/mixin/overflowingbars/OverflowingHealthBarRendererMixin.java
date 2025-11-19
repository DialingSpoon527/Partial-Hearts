package net.dialingspoon.partialhearts.mixin.overflowingbars;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.overflowingbars.client.handler.HealthBarRenderer;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HealthBarRenderer.class, remap = false)
public abstract class OverflowingHealthBarRendererMixin {
    @Shadow private int displayHealth;
    @Unique
    private float tempHealth;

    @Inject(method = "renderPlayerHealth", at = @At(value = "FIELD", target = "Lfuzs/overflowingbars/client/handler/HealthBarRenderer;displayHealth:I", opcode = Opcodes.PUTFIELD))
    private void prepareAbsorptionMask(GuiGraphics guiGraphics, int posX, int posY, Player player, ProfilerFiller profiler, CallbackInfo ci) {
        PatternManager.displayHealthFloat = player.getHealth();
    }

    @ModifyVariable(method = "renderHearts", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
    private boolean forceAbsorbBackground(boolean topHealth, @Local(ordinal = 12) int currentAbsorption, @Local(ordinal = 5, argsOnly = true) int currentAbsorptionHealth, @Local(ordinal = 13) int maxAbsorptionHealth) {
        if (currentAbsorption / 2 == ((((currentAbsorptionHealth - 1) % maxAbsorptionHealth)) / 2)) {
            tempHealth = Minecraft.getInstance().player.getAbsorptionAmount();
            return true;
        }
        return false;
    }

    @ModifyVariable(method = "renderHearts", at = @At(value = "STORE", ordinal = 1), ordinal = 2)
    private boolean forceBlinkBackground(boolean topHealth, @Local(ordinal = 9) int currentHeart) {
        if (currentHeart == (((displayHealth - 1) % 20) / 2)) {
            tempHealth = PatternManager.displayHealthFloat;
            return true;
        }
        return false;
    }

    @ModifyVariable(method = "renderHearts", at = @At(value = "STORE", ordinal = 2), ordinal = 2)
    private boolean forceHeartBackground(boolean topHealth, @Local(ordinal = 9) int currentHeart) {
        if (currentHeart == (int)((Minecraft.getInstance().player.getHealth() % 20) / 2)) {
            tempHealth = Minecraft.getInstance().player.getHealth();
            return true;
        }
        return false;
    }

    @ModifyArg(method = "renderHearts", at = @At(value = "INVOKE", target = "Lfuzs/overflowingbars/client/handler/HealthBarRenderer;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lfuzs/overflowingbars/client/handler/HealthBarRenderer$HeartType;IIZZZ)V"), index = 5, remap = true)
    private boolean notHalf(boolean half) {
        if (half)
            PatternManager.health = tempHealth;
        return false;
    }
}
