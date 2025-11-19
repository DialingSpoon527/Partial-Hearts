package net.dialingspoon.partialhearts.mixin.armorpointspp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cheos.armorpointspp.core.RenderableText;
import dev.cheos.armorpointspp.core.adapter.IMath;
import dev.cheos.armorpointspp.core.render.HealthTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HealthTextComponent.class, remap = false)
public abstract class APPPHealthTextComponentMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ldev/cheos/armorpointspp/core/adapter/IMath;ceil(F)I"))
    private int preserveDecimals(IMath instance, float f) {
        return (instance.ceil(f * 100f));
    }

    @WrapOperation(method = "render", at = @At(value = "NEW", target = "(Ljava/lang/Object;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 1))
    private RenderableText useDecimal0(Object i, Operation<RenderableText> original) {
        return original.call(toProperString(i));
    }
    @WrapOperation(method = "render", at = @At(value = "NEW", target = "(Ljava/lang/Object;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 3))
    private RenderableText useDecimal1(Object i, Operation<RenderableText> original) {
        return original.call(toProperString(i));
    }
    @WrapOperation(method = "render", at = @At(value = "NEW", target = "(Ljava/lang/Object;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 5))
    private RenderableText useDecimal2(Object i, Operation<RenderableText> original) {
        return original.call(toProperString(i));
    }

    @Unique
    private static String toProperString(Object i) {
        float f = ((int)i / 100f);
        String renderable = String.valueOf(f);
        if (Math.floor(f) == f) {
            renderable = String.valueOf((int) f);
        }
        return renderable;
    }
}
