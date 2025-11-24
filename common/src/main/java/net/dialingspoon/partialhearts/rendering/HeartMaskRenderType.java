package net.dialingspoon.partialhearts.rendering;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;

public class HeartMaskRenderType extends RenderType {
    static final BiFunction<ResourceLocation, Boolean, RenderType> OUTLINE = Util.memoize((resourceLocation, boolean_) -> RenderType.create("outline", 1536, boolean_ ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)).setOutputState(OUTLINE_TARGET).createCompositeState(RenderType.OutlineProperty.IS_OUTLINE)));
    private final CompositeState state;
    private final RenderPipeline renderPipeline;
    private final Optional<RenderType> outline;
    private final boolean isOutline;

    private float uvStartX, uvStartY;
    private float uvEndX, uvEndY;
    private float health;

    HeartMaskRenderType(String string, int i, boolean bl, boolean bl2, RenderPipeline renderPipeline, CompositeState compositeState, float health, float u1, float v1, float u2, float v2) {
        super(string, i, bl, bl2, () -> compositeState.states.forEach(RenderStateShard::setupRenderState), () -> compositeState.states.forEach(RenderStateShard::clearRenderState));
        this.state = compositeState;
        this.renderPipeline = renderPipeline;
        this.outline = compositeState.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE ? compositeState.textureState.cutoutTexture().map((resourceLocation) -> (RenderType)OUTLINE.apply(resourceLocation, renderPipeline.isCull())) : Optional.empty();
        this.isOutline = compositeState.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
        this.uvStartX = u1;
        this.uvStartY = v1;
        this.uvEndX = u2;
        this.uvEndY = v2;
        this.health = health;
    }

    public static HeartMaskRenderType heartMask(ResourceLocation texture, float health, float u1, float v1, float u2, float v2) {
        return new HeartMaskRenderType(
                "partialhearts_heart_mask",
                1536,
                false,
                false,
                PartialHearts.PIPELINE,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
                        .createCompositeState(true),
                health,
                u1,
                v1,
                u2,
                v2
        );
    }

    public Optional<RenderType> outline() {
        return this.outline;
    }

    public boolean isOutline() {
        return this.isOutline;
    }

    public RenderPipeline getRenderPipeline() {
        return this.renderPipeline;
    }

    public VertexFormat format() {
        return this.renderPipeline.getVertexFormat();
    }

    public VertexFormat.Mode mode() {
        return this.renderPipeline.getVertexFormatMode();
    }

    public void draw(MeshData meshData) {
        RenderPipeline renderPipeline = this.getRenderPipeline();
        this.setupRenderState();
        MeshData var3 = meshData;

        try {
            GpuBuffer gpuBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(meshData.vertexBuffer());
            GpuBuffer gpuBuffer2;
            VertexFormat.IndexType indexType;
            if (meshData.indexBuffer() == null) {
                RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
                gpuBuffer2 = autoStorageIndexBuffer.getBuffer(meshData.drawState().indexCount());
                indexType = autoStorageIndexBuffer.type();
            } else {
                gpuBuffer2 = renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(meshData.indexBuffer());
                indexType = meshData.drawState().indexType();
            }

            RenderTarget renderTarget = this.state.outputState.getRenderTarget();

            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(renderTarget.getColorTexture(), OptionalInt.empty(), renderTarget.useDepth ? renderTarget.getDepthTexture() : null, OptionalDouble.empty())) {
                renderPass.setPipeline(renderPipeline);
                renderPass.setVertexBuffer(0, gpuBuffer);
                if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                    renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
                }

                renderPass.setUniform("UVStart", uvStartX, uvStartY);
                renderPass.setUniform("UVEnd", uvEndX, uvEndY);
                renderPass.setUniform("MaskBits", PatternManager.createMaskBits(health));

                for(int i = 0; i < 12; ++i) {
                    GpuTexture gpuTexture = RenderSystem.getShaderTexture(i);
                    if (gpuTexture != null) {
                        renderPass.bindSampler("Sampler" + i, gpuTexture);
                    }
                }

                renderPass.setIndexBuffer(gpuBuffer2, indexType);
                renderPass.drawIndexed(0, meshData.drawState().indexCount());
            }
        } catch (Throwable var14) {
            if (meshData != null) {
                try {
                    var3.close();
                } catch (Throwable var11) {
                    var14.addSuppressed(var11);
                }
            }

            throw var14;
        }

        if (meshData != null) {
            meshData.close();
        }

        this.clearRenderState();
    }

    public RenderTarget getRenderTarget() {
        return this.state.outputState.getRenderTarget();
    }

    public String toString() {
        String var10000 = this.name;
        return "RenderType[" + var10000 + ":" + String.valueOf(this.state) + "]";
    }
}
