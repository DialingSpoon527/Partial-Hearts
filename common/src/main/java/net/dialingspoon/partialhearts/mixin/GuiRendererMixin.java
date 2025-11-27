package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.PatternManager;
import net.dialingspoon.partialhearts.rendering.HeartMaskRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Unique
    Queue<GpuBufferSlice> uniformSliceQueue = new ArrayDeque<>();

    @Inject(method = "executeDrawRange", at = @At(value = "HEAD"))
    private void uploadUniforms(Supplier<String> supplier, RenderTarget renderTarget, GpuBufferSlice gpuBufferSlice, GpuBufferSlice gpuBufferSlice2, GpuBuffer gpuBuffer, VertexFormat.IndexType indexType, int i, int j, CallbackInfo ci) {
        uniformSliceQueue.addAll(PatternManager.getUniformSlices());
    }

    @ModifyVariable(method = "executeDraw", at = @At(value = "STORE"))
    private RenderPipeline applyUniforms(RenderPipeline value, @Local(argsOnly = true) RenderPass renderPass) {
        if (value == PartialHearts.PIPELINE) {
            renderPass.setUniform("CustomUniform", uniformSliceQueue.poll());
        }
        return value;
    }

    @WrapOperation(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/GuiElementRenderState;pipeline()Lcom/mojang/blaze3d/pipeline/RenderPipeline;"))
    private RenderPipeline sortUniformsAndApplyPipeline(GuiElementRenderState instance, Operation<RenderPipeline> original) {
        if (instance instanceof HeartMaskRenderState heartMaskRenderState) {
            PatternManager.heartUniformQueue.add(heartMaskRenderState);
            return PartialHearts.PIPELINE;
        }
        return original.call(instance);
    }

    @ModifyExpressionValue(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/TextureSetup;equals(Ljava/lang/Object;)Z"))
    private boolean dontBatch(boolean original, @Local RenderPipeline renderPipeline) {
        if (renderPipeline == PartialHearts.PIPELINE) {
            return false;
        }
        return original;
    }
}
