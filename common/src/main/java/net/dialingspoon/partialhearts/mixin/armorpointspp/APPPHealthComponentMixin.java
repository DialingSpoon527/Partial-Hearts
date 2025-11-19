package net.dialingspoon.partialhearts.mixin.armorpointspp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.render.HealthComponent;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HealthComponent.class, remap = false)
public abstract class APPPHealthComponentMixin {

    @Shadow private int displayHealth;

    @Inject(method = "render", at = @At(value = "FIELD", target = "Ldev/cheos/armorpointspp/core/render/HealthComponent;displayHealth:I", opcode = Opcodes.PUTFIELD))
    private void recordDisplayHealth(RenderContext ctx, CallbackInfoReturnable<Boolean> cir) {
        PatternManager.displayHealthFloat = Minecraft.getInstance().player.getHealth();
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ldev/cheos/armorpointspp/core/texture/ITextureSheet;drawHeart(Ldev/cheos/armorpointspp/core/RenderContext;IIIZZZLdev/cheos/armorpointspp/core/texture/ITextureSheet$HeartStyle;)V"))
    private void prepareMask(ITextureSheet instance, RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, ITextureSheet.HeartStyle style, Operation<Void> original, @Local(name = "heartValue") int heartValue, @Local(name = "blink") boolean blink) {
        if ((heartValue /2) == (int)(Minecraft.getInstance().player.getHealth() /2)) {
            if (blink) {
                original.call(instance, ctx, x, y, spriteLevel, false, true, hardcore, style);
            } else if (spriteLevel > 0)
                original.call(instance, ctx, x, y, spriteLevel - 1, false, bright, hardcore, style);
            PatternManager.health = Minecraft.getInstance().player.getHealth();
        }
        if (bright && (heartValue /2) == ((int)(displayHealth - 1f)) /2) {
            if (spriteLevel > 0) {
                original.call(instance, ctx, x, y, spriteLevel - 1, false, bright, hardcore, style);
            }
            PatternManager.health = PatternManager.displayHealthFloat;
        }
        original.call(instance, ctx, x, y, spriteLevel, false, bright, hardcore, style);
    }
}
