package com.fuzs.consoleexperience.util;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

public class ControlHint {

    private final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation CONTROL_HINTS = new ResourceLocation(ConsoleExperience.MODID,"textures/gui/control_tip.png");
    private final int iconWidth = 11;
    private final int iconHeight = 16;
    private final int iconInset = 5;

    private final KeyBinding key;
    private final TranslationTextComponent text;
    private final Side side;

    public ControlHint(KeyBinding keyBinding, TranslationTextComponent component, Side side) {
        this.key = keyBinding;
        this.text = component;
        this.side = side;
    }

    public String getKey() {
        return this.key.getLocalizedName().toUpperCase(Locale.ROOT);
    }

    public String getDescription() {
        return this.text.getFormattedText();
    }

    public Side getSide() {
        return this.side;
    }

    public int getIcon() {
        int i = this.key.getKey().getKeyCode();
        return i <= 2 ? i : -1;
    }

    public boolean isMouse() {
        return this.getIcon() != -1;
    }

    public int getWidth() {
        int i = this.mc.fontRenderer.getStringWidth(this.getKey());
        int j = this.mc.fontRenderer.getStringWidth(this.getDescription());
        return this.isMouse() ? this.iconWidth + j + 6 :
                this.iconInset * 2 + Math.max(i, this.iconHeight - this.iconInset * 2) + j + 6;
    }

    public void draw(int posX, int posY) {

        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CONTROL_HINTS);

        if (this.isMouse()) {

            AbstractGui.blit(posX, posY, 200 + this.iconWidth * this.getIcon(), 0, this.iconWidth, this.iconHeight, 256, 256);
            posX += this.iconWidth + 3;

        } else {

            int i = this.mc.fontRenderer.getStringWidth(this.getKey());
            int length = Math.max(i, this.iconHeight - this.iconInset * 2) / 2;
            AbstractGui.blit(posX, posY, 0, 0, this.iconInset + length, this.iconHeight, 256, 256);
            AbstractGui.blit(posX + this.iconInset + length, posY, 200 - this.iconInset - length, 0, this.iconInset + length, this.iconHeight, 256, 256);
            this.mc.fontRenderer.drawStringWithShadow(this.getKey(), posX + this.iconInset + Math.max(0, length - i / 2.0F), posY + 4, -1);
            posX += this.iconInset * 2 + length * 2 + 3;

        }

        this.mc.fontRenderer.drawStringWithShadow(this.getDescription(), posX, posY + 4, -1);

    }

    public enum Side {
        LEFT,
        RIGHT
    }

}
