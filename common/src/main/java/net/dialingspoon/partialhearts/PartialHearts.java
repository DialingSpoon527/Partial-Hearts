package net.dialingspoon.partialhearts;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public final class PartialHearts {
    public static final String MOD_ID = "partialhearts";
    public static RenderPipeline PIPELINE;

    public static void init() {
        RenderPipeline pipeline = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
                .withLocation(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "pipeline/heart_mask"))
                .withVertexShader(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "core/heart_mask"))
                .withFragmentShader(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "core/heart_mask"))
                .withUniform("UVStart",  UniformType.VEC2)
                .withUniform("UVEnd",    UniformType.VEC2)
                .withUniform("MaskBits", UniformType.IVEC3)
                .withSampler("Sampler0")
                .withBlend(BlendFunction.TRANSLUCENT)
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
                .build();

        PartialHearts.PIPELINE = RenderPipelines.register(pipeline);
    }
}
