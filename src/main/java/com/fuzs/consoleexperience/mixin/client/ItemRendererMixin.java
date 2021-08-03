package com.fuzs.consoleexperience.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements IResourceManagerReloadListener {

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("HEAD"))
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callbackInfo) {

        if (!stack.isEmpty() && stack.getItem() instanceof PotionItem) {

            final int dotCount = getDotCount(PotionUtils.getEffectsFromStack(stack));
            if (dotCount > 0) {

                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();

                this.draw(bufferbuilder, xPosition + 3, yPosition + 13, 11, 2, 0, 0, 0, 255);
                final int color = TextFormatting.AQUA.getColor();
                for (int i = 0; i < dotCount; i++) {

                    this.draw(bufferbuilder, xPosition + 3 + 3 * i, yPosition + 13, 2, 2, color >> 16 & 255, color >> 8 & 255, color & 255, 255);
                }

                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    @Shadow
    private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {

        throw new IllegalStateException();
    }

    private static int getDotCount(List<EffectInstance> list) {

        if (list.isEmpty()) {

            return 0;
        }

        int dotCount = 0;
        for (EffectInstance effectInstance : list) {

            dotCount += effectInstance.getAmplifier() + 1;
        }

        return Math.min(dotCount, 4);
    }

}
