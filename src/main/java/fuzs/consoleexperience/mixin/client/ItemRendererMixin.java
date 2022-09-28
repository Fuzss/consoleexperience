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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin implements IResourceManagerReloadListener {

    @Inject(method = "renderGuiItem(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/renderer/model/IBakedModel;)V", at = @At(value = "NEW", args = "class=com/mojang/blaze3d/matrix/MatrixStack"))
    protected void renderGuiItem(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_, CallbackInfo callbackInfo) {

        RenderSystem.color4f(1.0F, 1.0F, 0.0F, 0.2F);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    public void renderGuiItemDecorations(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callbackInfo) {

        if (stack.getItem() instanceof PotionItem) {

            List<EffectInstance> mobEffects = PotionUtils.getMobEffects(stack);
            int dotCount = getDotCount(mobEffects);
            if (dotCount > 0) {

                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();

                this.fillRect(bufferbuilder, xPosition + 3, yPosition + 13, 11, 2, 0, 0, 0, 255);
//                int color = TextFormatting.AQUA.getColor();

                TextFormatting[] colors = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD, TextFormatting.GREEN, TextFormatting.YELLOW, TextFormatting.RED};

//                int[] dotColors = new int[4];
//                dotColors[0] = PotionUtils.getColor(stack);
                Potion potion = PotionUtils.getPotion(stack);
                ResourceLocation potionKey = Registry.POTION.getKey(potion);
//                color = potionKey.getPath().startsWith("strong_") ? TextFormatting.GOLD.getColor() : (potionKey.getPath().startsWith("long_") ? TextFormatting.RED.getColor() : TextFormatting.AQUA.getColor());
//                dotColors[2] = PotionUtils.getMobEffects(stack).stream().map(EffectInstance::getEffect).anyMatch(((Predicate<Effect>) Effect::isBeneficial).negate()) ? TextFormatting.RED.getColor() : TextFormatting.BLUE.getColor();
//                dotColors[3] = IntStream.range(0, 16).mapToObj(i -> TextFormatting.values()[15 - i]).skip(dotCount % 16).findFirst().map(TextFormatting::getColor).orElse(TextFormatting.AQUA.getColor());

//                dotCount += mobEffects.size() - 1;
//                if (potionKey.getPath().startsWith("strong_")) {
//                    dotCount += 4;
//                }
//                if (potionKey.getPath().startsWith("long_")) {
//                    dotCount += 8;
//                }

                for (int i = 0; i < Math.min(4, dotCount); i++) {
//                    if ((dotCount >> i & 1) == 1) {
////                    int color = dotColors[i];
//                    }

                    int color = colors[Math.min((dotCount - i - 1) / 4, colors.length - 1)].getColor();
                    this.fillRect(bufferbuilder, xPosition + 3 + 3 * i, yPosition + 13, 2, 2, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 0xFF);
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
        return list.stream().mapToInt(EffectInstance::getAmplifier).map(i -> i + 1).sum();
    }

}
