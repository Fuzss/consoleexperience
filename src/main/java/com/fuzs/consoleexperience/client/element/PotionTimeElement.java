package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.mixin.client.accessor.IDisplayEffectsScreenAccessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class PotionTimeElement extends GameplayElement {

    private static final ResourceLocation EFFECT_BACKGROUND = new ResourceLocation(ConsoleExperience.MODID,"textures/gui/mob_effect_background.png");
    private static final ResourceLocation TINY_NUMBERS_TEXTURE = new ResourceLocation(ConsoleExperience.MODID,"textures/font/tiny_numbers.png");

    @Override
    public void setup() {

        this.addListener(this::onPotionShift);
        this.addListener(this::onInitGuiPost);
        this.addListener(this::onDrawScreenPost);
        this.addListener(this::onRenderGameOverlayPre);
        this.addListener(this::onRenderGameOverlayText);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Potion Time";
    }

    @Override
    public String getDescription() {

        return "Add remaining duration to potion icons shown in-game.";
    }

    private void onPotionShift(final GuiScreenEvent.PotionShiftEvent evt) {

        evt.setCanceled(true);
    }

    private void onInitGuiPost(final GuiScreenEvent.InitGuiEvent.Post evt) {

        if (evt.getGui() instanceof DisplayEffectsScreen) {

            // disable vanilla rendering in creative mode inventory, survival inventory has to be disabled via mixin
            // this is not needed by us, we just check before rendering as survival inventory does
            ((IDisplayEffectsScreenAccessor) evt.getGui()).setHasActivePotionEffects(false);
        }
    }

    private void onDrawScreenPost(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (evt.getGui() instanceof ContainerScreen && (!(evt.getGui() instanceof IRecipeShownListener) || !((IRecipeShownListener) evt.getGui()).getRecipeGui().isVisible())) {

            int guiLeft = ((ContainerScreen<?>) evt.getGui()).getGuiLeft();
            this.drawPotionIcons(evt.getMatrixStack(), guiLeft, ((ContainerScreen<?>) evt.getGui()).getGuiTop(), evt.getMouseX(), evt.getMouseY(), Math.max(1, guiLeft / 30)).ifPresent(effectInstance -> {

                if (effectInstance.shouldRenderInvText()) {

                    String potionName = effectInstance.getPotion().getName();
                    IFormattableTextComponent textComponent = new TranslationTextComponent(potionName);
                    if (effectInstance.getAmplifier() >= 1 && effectInstance.getAmplifier() <= 9) {

                        textComponent.appendString(" ").append(new TranslationTextComponent("enchantment.level." + (effectInstance.getAmplifier() + 1)));
                    }

                    List<ITextComponent> list = Lists.newArrayList(textComponent);
                    // description may be provided by Potion Descriptions mod
                    String descriptionKey = "description." + potionName;
                    // hasKey
                    if (LanguageMap.getInstance().func_230506_b_(descriptionKey)) {

                        list.add(new TranslationTextComponent(descriptionKey).mergeStyle(TextFormatting.GRAY));
                    }

                    // renderTooltip
                    evt.getGui().func_243308_b(evt.getMatrixStack(), list, evt.getMouseX(), evt.getMouseY());
                }
            });
        }
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != ElementType.POTION_ICONS) {

            return;
        }

        evt.setCanceled(true);
    }

    private void onRenderGameOverlayText(final RenderGameOverlayEvent.Text evt) {

        // use this event so potion icons are drawn behind the debug menu like in vanilla
        this.drawPotionIcons(evt.getMatrixStack(), evt.getWindow().getScaledWidth(), 1, -1, -1, -1);
    }

    private Optional<EffectInstance> drawPotionIcons(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, final int maxWidth) {

        assert this.mc.player != null;
        Collection<EffectInstance> activePotionEffects = this.mc.player.getActivePotionEffects();
        Optional<EffectInstance> hoveredEffect = Optional.empty();
        if (!activePotionEffects.isEmpty()) {

            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            final int beneficialRows = (int) Math.ceil((double) activePotionEffects.stream()
                    .map(EffectInstance::getPotion)
                    .filter(Effect::isBeneficial)
                    .count() / maxWidth);
            int beneficialCounter = 0;
            int harmfulCounter = 0;
            PotionSpriteUploader potionspriteuploader = this.mc.getPotionSpriteUploader();
            List<Runnable> effects = Lists.newArrayListWithExpectedSize(activePotionEffects.size());
            for (EffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(activePotionEffects)) {

                // Rebind in case previous renderHUDEffect changed texture
                this.mc.getTextureManager().bindTexture(EFFECT_BACKGROUND);
                if (maxWidth != -1 || effectinstance.shouldRenderHUD() && effectinstance.isShowIcon()) {

                    Effect effect = effectinstance.getPotion();
                    int width = x;
                    int height = y;
                    if (this.mc.isDemo()) {

                        height += 15;
                    }

                    if (effect.isBeneficial()) {

                        if (maxWidth != -1) {

                            height += 25 * (beneficialCounter / maxWidth);
                        }

                        beneficialCounter++;
                        width -= 30 * (maxWidth != -1 ? (beneficialCounter - 1) % maxWidth + 1 : beneficialCounter);
                    } else {

                        height += maxWidth != -1 ? 25 * beneficialRows + 1 : 26;
                        if (maxWidth != -1) {

                            height += 25 * (harmfulCounter / maxWidth);
                        }

                        harmfulCounter++;
                        width -= 30 * (maxWidth != -1 ? (harmfulCounter - 1) % maxWidth + 1 : harmfulCounter);
                    }

                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    float alpha = 1.0F;
                    if (effectinstance.isAmbient()) {

                        AbstractGui.blit(matrixStack, width, height, 29, 0, 29, 24, 256, 256);
                    } else {

                        AbstractGui.blit(matrixStack, width, height, 0, 0, 29, 24, 256, 256);
                        if (effectinstance.getDuration() <= 200) {

                            int duration = 10 - effectinstance.getDuration() / 20;
                            alpha = MathHelper.clamp((float) effectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) effectinstance.getDuration() * (float)Math.PI / 5.0F) * MathHelper.clamp((float) duration / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    if (mouseX >= width && mouseX <= width + 30 && mouseY > height && mouseY <= height + 26) {

                        hoveredEffect = Optional.of(effectinstance);
                    }

                    TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
                    effects.add(this.getEffectRenderer(matrixStack, effectinstance, textureatlassprite, width, height, alpha));
                    effectinstance.renderHUDEffect(this.mc.ingameGUI, matrixStack, width, height, this.mc.ingameGUI.getBlitOffset(), alpha);
                }
            }

            effects.forEach(Runnable::run);
            RenderSystem.enableDepthTest();
        }

        return hoveredEffect;
    }

    private Runnable getEffectRenderer(MatrixStack matrixStack, EffectInstance effectinstance, TextureAtlasSprite textureatlassprite, int width, int height, float alpha) {

        return () -> {

            this.mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            AbstractGui.blit(matrixStack, width + 5, height + (effectinstance.isAmbient() ? 3 : 2), this.mc.ingameGUI.getBlitOffset(), 18, 18, textureatlassprite);
            int potionColor = this.isColorTooDark(PotionUtils.getPotionColorFromEffectList(Lists.newArrayList(effectinstance)));
//            potionColor = 0xFFFFFF;
            if (effectinstance.getAmplifier() > 0 && effectinstance.getAmplifier() <= 9) {

                this.mc.getTextureManager().bindTexture(TINY_NUMBERS_TEXTURE);
                float r = (potionColor >> 16 & 255) / 255.0F;
                float g = (potionColor >> 8 & 255) / 255.0F;
                float b = (potionColor >> 0 & 255) / 255.0F;
                RenderSystem.color4f(r * 0.25F, g * 0.25F, b * 0.25F, 1.0F);
                AbstractGui.blit(matrixStack, width + 24, height + 3, 5 * (effectinstance.getAmplifier() + 1), 0, 3, 5, 256, 256);
                RenderSystem.color4f(r, g, b, 1.0F);
                AbstractGui.blit(matrixStack, width + 23, height + 2, 5 * (effectinstance.getAmplifier() + 1), 0, 3, 5, 256, 256);
            }

            if (!effectinstance.isAmbient()) {

                String durationString = EffectUtils.getPotionDurationString(effectinstance, 1.0F);
                durationString = durationString.equals("**:**") ? "\u221e" : durationString;
                StringTextComponent component = new StringTextComponent(durationString);
                AbstractGui.drawCenteredString(matrixStack, this.mc.fontRenderer, component, width + 15, height + 14, potionColor);
            }
        };
    }
    
    @SuppressWarnings("PointlessBitwiseExpression")
    private int isColorTooDark(int potionColor) {

        int r = potionColor >> 16 & 255;
        int g = potionColor >> 8 & 255;
        int b = potionColor >> 0 & 255;

        return r + g + b < 128 ? 8355711 : potionColor;
    }

}
