package com.fuzs.consoleexperience.util;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class CloseButton extends Button {

    private static final ResourceLocation CLOSE_BUTTON = new ResourceLocation(ConsoleExperience.MODID, "textures/gui/close_button.png");

    private final int posX;
    private final int posY;
    private final ContainerScreen parent;
    // mainly for chests
    private final boolean isScreenSmall;

    public CloseButton(int posX, int posY, IPressable onPress, ContainerScreen screen) {
        super(0, 0, 15, 15, "", onPress);
        this.parent = screen;
        this.isScreenSmall = this.parent.getYSize() != 166;
        this.posX = posX;
        this.posY = this.isScreenSmall ? posY - 3 : posY;
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {

        this.x = this.parent.getGuiLeft() + this.parent.getXSize() - this.posX - this.width;
        this.y = this.parent.getGuiTop() + this.posY;

        Minecraft.getInstance().getTextureManager().bindTexture(CLOSE_BUTTON);

        RenderSystem.disableDepthTest();
        if (this.isScreenSmall) {
            blit(this.x + 1, this.y + 1, 1, this.isHovered() ? this.height + 1 : 1, this.width - 2, this.height - 2);
        } else {
            blit(this.x, this.y, 0, this.isHovered() ? this.height : 0, this.width, this.height);
        }
        RenderSystem.enableDepthTest();

    }

}
