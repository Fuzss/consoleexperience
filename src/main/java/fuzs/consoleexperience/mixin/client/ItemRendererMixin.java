package fuzs.consoleexperience.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements IResourceManagerReloadListener {

    @Inject(method = "renderGuiItem(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/renderer/model/IBakedModel;)V", at = @At(value = "NEW", args = "class=com/mojang/blaze3d/matrix/MatrixStack"))
    protected void renderGuiItem(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_, CallbackInfo callbackInfo) {

        RenderSystem.color4f(1.0F, 1.0F, 0.0F, 0.2F);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    public void renderGuiItemDecorations(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callbackInfo) {

        if (!stack.isEmpty() && stack.getItem() instanceof PotionItem) {

            final int dotCount = getDotCount(PotionUtils.getMobEffects(stack));
            if (dotCount > 0) {

                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();

                this.fillRect(bufferbuilder, xPosition + 3, yPosition + 13, 11, 2, 0, 0, 0, 255);
                final int color = TextFormatting.AQUA.getColor();
                for (int i = 0; i < dotCount; i++) {

                    this.fillRect(bufferbuilder, xPosition + 3 + 3 * i, yPosition + 13, 2, 2, color >> 16 & 255, color >> 8 & 255, color & 255, 255);
                }

                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    @Shadow
    private void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {

        throw new IllegalStateException();
    }

    @Unique
    private int getDotCount(List<EffectInstance> list) {

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
