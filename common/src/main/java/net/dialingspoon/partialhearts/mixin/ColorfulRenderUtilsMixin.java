package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import terrails.colorfulhearts.render.RenderUtils;

@Mixin(value = RenderUtils.class)
public class ColorfulRenderUtilsMixin {

    @WrapMethod(method = "drawTexture(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIIIIIIIII)V")
    private static void DrawModifiedTexture(PoseStack poseStack, int x1, int x2, int y1, int y2, int u1, int u2,
                                             int v1, int v2, int red, int green, int blue, int alpha, Operation<Void> original) {
        if (PatternManager.health != -1) {
            NativeImage cellImage = PatternManager.getImage(PartialHearts.CAPTURED_SPRITE, u1, v1);

            PatternManager.prepareHeart(cellImage, PatternManager.health);

            DynamicTexture dynamicTexture = new DynamicTexture(cellImage);
            ResourceLocation textureLocation = new ResourceLocation(PartialHearts.MOD_ID, "dynamic_heart_texture");
            Minecraft.getInstance().getTextureManager().register(textureLocation, dynamicTexture);

            cellImage.close();

            RenderSystem.setShaderTexture(0, textureLocation);
            original.call(poseStack, x1, x2, y1, y2, 0, 256, 0, 256, red, green, blue, alpha);
        } else {
            original.call(poseStack, x1, x2, y1, y2, u1, u2, v1, v2, red, green, blue, alpha);
        }
    }
}

