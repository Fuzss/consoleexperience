package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class PotionTimeElement extends GameplayElement {

    private static final ResourceLocation POTION_BACKGROUND = new ResourceLocation(ConsoleExperience.MODID,"textures/gui/mob_effect_background.png");

    @Override
    public void setup() {

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

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != ElementType.POTION_ICONS) {

            return;
        }

        evt.setCanceled(true);
    }

    private void onRenderGameOverlayText(final RenderGameOverlayEvent.Text evt) {

        // use this event so potion icons are drawn behind the debug menu like in vanilla
        assert this.mc.player != null;
        Collection<EffectInstance> activePotionEffects = this.mc.player.getActivePotionEffects();
        if (!activePotionEffects.isEmpty()) {

            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            int beneficialCounter = 0;
            int harmfulCounter = 0;
            PotionSpriteUploader potionspriteuploader = this.mc.getPotionSpriteUploader();
            List<Runnable> effects = Lists.newArrayListWithExpectedSize(activePotionEffects.size());
            for (EffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(activePotionEffects)) {

                // Rebind in case previous renderHUDEffect changed texture
                this.mc.getTextureManager().bindTexture(POTION_BACKGROUND);
                if (effectinstance.shouldRenderHUD() && effectinstance.isShowIcon()) {

                    Effect effect = effectinstance.getPotion();
                    int width = evt.getWindow().getScaledWidth();
                    int height = 1;
                    if (this.mc.isDemo()) {
                        height += 15;
                    }

                    if (effect.isBeneficial()) {

                        beneficialCounter++;
                        width = width - 30 * beneficialCounter;
                    } else {

                        harmfulCounter++;
                        width = width - 30 * harmfulCounter;
                        height += 26;
                    }

                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    float alpha = 1.0F;
                    if (effectinstance.isAmbient()) {

                        AbstractGui.blit(evt.getMatrixStack(), width, height, 29, 0, 29, 24, 256, 256);
                    } else {

                        AbstractGui.blit(evt.getMatrixStack(), width, height, 0, 0, 29, 24, 256, 256);
                        if (effectinstance.getDuration() <= 200) {

                            int duration = 10 - effectinstance.getDuration() / 20;
                            alpha = MathHelper.clamp((float) effectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) effectinstance.getDuration() * (float)Math.PI / 5.0F) * MathHelper.clamp((float) duration / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
                    this.addEffectToList(evt.getMatrixStack(), effects, effectinstance, textureatlassprite, width, height, alpha);
                    effectinstance.renderHUDEffect(this.mc.ingameGUI, evt.getMatrixStack(), width, height, this.mc.ingameGUI.getBlitOffset(), alpha);
                }
            }

            effects.forEach(Runnable::run);
            RenderSystem.enableDepthTest();
        }
    }

    private void addEffectToList(MatrixStack matrixStack, List<Runnable> effects, EffectInstance effectinstance, TextureAtlasSprite textureatlassprite, int width, int height, float alpha) {

        effects.add(() -> {

            this.mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            AbstractGui.blit(matrixStack, width + 5, height + (effectinstance.isAmbient() ? 3 : 2), this.mc.ingameGUI.getBlitOffset(), 18, 18, textureatlassprite);
            if (!effectinstance.isAmbient()) {

                StringTextComponent component = new StringTextComponent(EffectUtils.getPotionDurationString(effectinstance, 1.0F));
                int potionColor = PotionUtils.getPotionColorFromEffectList(Collections.singleton(effectinstance));
                AbstractGui.drawCenteredString(matrixStack, this.mc.fontRenderer, component, width + 15, height + 14, potionColor);
            }
        });
    }

}
