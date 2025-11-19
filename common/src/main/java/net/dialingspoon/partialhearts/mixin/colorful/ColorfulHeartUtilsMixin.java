package net.dialingspoon.partialhearts.mixin.colorful;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.render.HeartUtils;

@Mixin(value = HeartUtils.class, remap = false)
public abstract class ColorfulHeartUtilsMixin {

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE"), ordinal = 3)
    private static int colorfulhearts$modifyTopHealth(int topHealth, @Local(name = "health") int health) {
        if (topHealth == 0 && health > 20) {
            topHealth = 20;
        }
        return topHealth;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE"), ordinal = 7)
    private static int colorfulhearts$modifyTopAbsorbing(int topAbsorbing, @Local(name = "absorbing") int absorbing, @Local(name = "maxAbsorbing") int maxAbsorbing) {
        if (topAbsorbing == 0 && absorbing > maxAbsorbing) {
            topAbsorbing = maxAbsorbing;
        }
        return topAbsorbing;
    }

    @WrapOperation(method = "calculateHearts",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(F)I"), remap = true)
    private static int colorfulhearts$ensureIndex(float f, Operation<Integer> original) {
        int floored = original.call(f);
        return f == floored ? floored - 1 : floored;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private static boolean forceColorfulBackground(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "topHealth") int topHealth) {
        return shouldRenderBack(half, index, topHealth);
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private static boolean forceFullVanillaBackground(boolean half) {
        return false;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private static boolean forceVanillaBackground(boolean half) {
        return false;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    private static boolean forceContainersFull(boolean half) {
        return false;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 3), ordinal = 0)
    private static boolean forceColorfulAbsorptionBackground(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "topAbsorbing") int topAbsorbing) {
        return shouldRenderBack(half, index, topAbsorbing);
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 4), ordinal = 0)
    private static boolean forceAbsorbingContainersFull(boolean half) {
        return false;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 5), ordinal = 0)
    private static boolean forceOverlayColorfulBackground(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "topHealth") int topHealth) {
        return shouldRenderBack(half, index, topHealth);
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 1), ordinal = 1)
    private static boolean forceOverlayFullVanillaBackground(boolean half) {
        return false;
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 6), ordinal = 0)
    private static boolean forceOverlayVanillaBackground(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "health") int health) {
        return shouldRenderBack(half, index, health);
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 7), ordinal = 0)
    private static boolean forceOverlayColorfulAbsorptionBackground(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "topAbsorbing") int topAbsorbing) {
        return shouldRenderBack(half, index, topAbsorbing);
    }

    @ModifyVariable(method = "calculateHearts",
            at = @At(value = "STORE", ordinal = 8), ordinal = 0)
    private static boolean forceOverlayAbsorbingContainersFull(boolean half, @Local(name = "i") int index,
                                                     @Local(name = "absorbing") int absorbing) {
        return shouldRenderBack(half, index, absorbing);
    }

    @Unique
    private static boolean shouldRenderBack(boolean half, int heart, int lastHeart) {
        return heart == (lastHeart - 1) / 2 || half;
    }
}
