package net.dialingspoon.partialhearts.mixin.immediatelyfast;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiGraphics.class)
public abstract class ImmediatelyFastGuiGraphicsMixin {

    @WrapMethod(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V")
    private void stopBatching(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, Operation<Void> original) {
        boolean partialHeart = PatternManager.health != -1 && ImmediatelyFastApi.getApiImpl().getBatching().isHudBatching();

        if (partialHeart)
            ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();

        original.call(resourceLocation, i, j, k, l, m, f, g, h, n);

        if (partialHeart)
            ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
    }

}
