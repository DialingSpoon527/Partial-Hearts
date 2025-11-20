package net.dialingspoon.partialhearts.mixin.armorpointspp;

import com.llamalad7.mixinextras.sugar.Local;
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
        return instance.ceil(f * 100f);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ldev/cheos/armorpointspp/core/RenderableText;append(Ldev/cheos/armorpointspp/core/RenderableText;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 0))
    private RenderableText useDecimal0(RenderableText instance, RenderableText text, @Local(name = "health") int health) {
        return instance.append(new RenderableText(toProperString(health)).padRight(1.0F).withColor(text.getColor()));
    }
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ldev/cheos/armorpointspp/core/RenderableText;append(Ldev/cheos/armorpointspp/core/RenderableText;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 2))
    private RenderableText useDecimal1(RenderableText instance, RenderableText text, @Local(name = "maxHp") int maxHp) {
        return instance.append(new RenderableText(toProperString(maxHp)).padRight(1.0F).withColor(text.getColor()));
    }
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ldev/cheos/armorpointspp/core/RenderableText;append(Ldev/cheos/armorpointspp/core/RenderableText;)Ldev/cheos/armorpointspp/core/RenderableText;", ordinal = 4))
    private RenderableText useDecimal2(RenderableText instance, RenderableText text, @Local(name = "absorb") int absorb) {
        return instance.append(new RenderableText(toProperString(absorb)).padRight(1.0F).withColor(text.getColor()));
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
