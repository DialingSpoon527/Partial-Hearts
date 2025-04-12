package net.dialingspoon.partialhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dialingspoon.partialhearts.PartialHearts;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RenderSystem.class)
public class RenderSystemMixin {
    
    @WrapMethod(method = "setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
    private static void collectShaderTexture(int i, ResourceLocation resourceLocation, Operation<Void> original) {
        if (i == 0 && !resourceLocation.equals(new ResourceLocation(PartialHearts.MOD_ID, "dynamic_heart_texture"))) {
            PartialHearts.CAPTURED_SPRITE = resourceLocation;
        }
        original.call(i, resourceLocation);
    }
}

