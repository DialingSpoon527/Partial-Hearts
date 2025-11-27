package net.dialingspoon.partialhearts.rendering;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;

public class DynamicCustomUniforms implements AutoCloseable {
    public static final int UBO_SIZE = new Std140SizeCalculator().putMat4f().putVec4().putVec3().putMat4f().putFloat().get();
    private static final int INITIAL_CAPACITY = 2;
    private final DynamicUniformStorage<HeartMaskRenderState> storage = new DynamicUniformStorage<>("Dynamic Data UBO", UBO_SIZE, INITIAL_CAPACITY);

    public void reset() {
        this.storage.endFrame();
    }

    @Override
    public void close() {
        this.storage.close();
    }

    public GpuBufferSlice[] writeHeartUniforms(HeartMaskRenderState... Data) {
        return this.storage.writeUniforms(Data);
    }
}