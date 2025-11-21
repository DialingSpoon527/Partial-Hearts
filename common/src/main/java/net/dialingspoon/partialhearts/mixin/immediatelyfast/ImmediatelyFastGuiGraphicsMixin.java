package net.dialingspoon.partialhearts.mixin.immediatelyfast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class ImmediatelyFastGuiGraphicsMixin {

    @Shadow @Final private PoseStack pose;

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V", at = @At("HEAD"), cancellable = true)
    private void stopBatching(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, CallbackInfo ci) {
        if (PatternManager.health != -1) {
            ci.cancel();

            RenderSystem.setShaderTexture(0, resourceLocation);
            RenderSystem.setShader(PatternManager.getShader(PatternManager.health, f, g, h, n));
            Matrix4f matrix4f = this.pose.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.addVertex(matrix4f, i, k, m).setUv(f, h);
            bufferBuilder.addVertex(matrix4f, i, l, m).setUv(f, n);
            bufferBuilder.addVertex(matrix4f, j, l, m).setUv(g, n);
            bufferBuilder.addVertex(matrix4f, j, k, m).setUv(g, h);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            PatternManager.health = -1;
        }
    }
}
