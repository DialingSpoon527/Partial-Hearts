package net.dialingspoon.partialhearts.mixin;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.dialingspoon.partialhearts.PartialHearts;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CoreShaders.class)
public abstract class CoreShadersMixin {

    @Shadow @Final private static List<ShaderProgram> PROGRAMS;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void registerShader(CallbackInfo ci) {
        ShaderProgram shaderProgram = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "core/heart_mask"), DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY);
        PROGRAMS.add(shaderProgram);
        PartialHearts.SHADER = shaderProgram;
    }
}
