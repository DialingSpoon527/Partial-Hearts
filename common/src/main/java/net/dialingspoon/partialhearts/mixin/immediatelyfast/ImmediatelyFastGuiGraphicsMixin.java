package net.dialingspoon.partialhearts.mixin.immediatelyfast;

import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class ImmediatelyFastGuiGraphicsMixin {
    @Unique
    boolean partialHeart;

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V", at = @At("HEAD"))
    private void stopBatching(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, CallbackInfo ci) {
        partialHeart = PatternManager.health != -1 && ImmediatelyFastApi.getApiImpl().getBatching().isHudBatching();
        if (partialHeart)
            ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();
    }

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V", at = @At("TAIL"))
    private void startBatching(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, CallbackInfo ci) {
        if (partialHeart){
            ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
            partialHeart = false;
        }
    }

}
