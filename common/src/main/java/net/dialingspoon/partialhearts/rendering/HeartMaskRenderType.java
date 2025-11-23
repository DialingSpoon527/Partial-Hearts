package net.dialingspoon.partialhearts.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

public class HeartMaskRenderType {
    public static RenderType heartMask(ResourceLocation texture) {
        return RenderType.create(
                "partialhearts_heart_mask",
                DefaultVertexFormat.POSITION_TEX_COLOR,
                VertexFormat.Mode.QUADS,
                786432,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
                        .setShaderState(new RenderStateShard.ShaderStateShard(PartialHearts.SHADER))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .createCompositeState(false)
        );
    }
}

