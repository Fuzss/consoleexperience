package com.fuzs.consoleexperience.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;

public class CloseButton extends Button {

    public CloseButton(int posX, int posY, IPressable onPress) {
        super(posX, posY, 15, 15, "x", onPress);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        if (this.isHovered) {
            fill(this.x, this.y, this.x + this.width, this.y + this.height, 11382189 + (255 << 24));
        }

        Minecraft.getInstance().fontRenderer.drawString(this.getMessage(), this.x + 5, this.y + 3,
                this.isHovered ? 16777215 : 4210752 | MathHelper.ceil(this.alpha * 255.0F) << 24);

    }

}
