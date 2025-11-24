package net.dialingspoon.partialhearts.mixin.immediatelyfast;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Function;

@Mixin(GuiGraphics.class)
public abstract class ImmediatelyFastGuiGraphicsMixin {

    @WrapMethod(method = "innerBlit")
    private void changeShader(Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l, float f, float g, float h, float m, int n, Operation<Void> original) {
        boolean partial = PatternManager.health != -1;
        original.call(function, resourceLocation, i,j,k,l,f,g,h,m,n);

        if (partial && BatchingBuffers.isHudBatching())
            BatchingBuffers.tryForceDrawHudBuffers();

    }
}
