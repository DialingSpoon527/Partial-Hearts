package net.dialingspoon.partialhearts.rendering;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector3i;

import java.nio.ByteBuffer;

public record HeartMaskRenderState(
        Vector3i maskBits,
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x0,
        int y0,
        int x1,
        int y1,
        float u0,
        float u1,
        float v0,
        float v1,
        int color,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
    ) implements GuiElementRenderState, DynamicUniformStorage.DynamicUniform {

    public HeartMaskRenderState(BlitRenderState blitRenderState, float health) {
        this(blitRenderState, PatternManager.createMaskBits(health));
    }

    public HeartMaskRenderState(BlitRenderState blitRenderState, int[] maskBitArray) {
        this(new Vector3i(maskBitArray[0], maskBitArray[1], maskBitArray[2]), blitRenderState.pipeline(), blitRenderState.textureSetup(), blitRenderState.pose(), blitRenderState.x0(), blitRenderState.y0(), blitRenderState.x1(), blitRenderState.y1(), blitRenderState.u0(), blitRenderState.u1(), blitRenderState.v0(), blitRenderState.v1(), blitRenderState.color(), blitRenderState.scissorArea(), blitRenderState.bounds());
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer, float f) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setUv(this.u0(), this.v0()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setUv(this.u0(), this.v1()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setUv(this.u1(), this.v1()).setColor(this.color());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setUv(this.u1(), this.v0()).setColor(this.color());
    }

    @Override
    public void write(ByteBuffer byteBuffer) {
        Std140Builder.intoBuffer(byteBuffer)
                .putVec2(new Vector2f(u0, v0))
                .putVec2(new Vector2f(u1, v1))
                .putIVec3(maskBits);
    }
}
