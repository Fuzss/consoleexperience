package com.fuzs.consoleexperience.client.gui.button;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class CloseButton extends Button {

    private static final ResourceLocation CLOSE_BUTTON = new ResourceLocation(ConsoleExperience.MODID, "textures/gui/close_button.png");

    private final int posX;
    private final int posY;
    private final ContainerScreen<Container> parent;
    // mainly for chests
    private final boolean isTopSmall;

    public CloseButton(int posX, int posY, IPressable onPress, ContainerScreen<Container> screen) {

        super(0, 0, 15, 15, StringTextComponent.EMPTY, onPress);
        this.parent = screen;
        this.isTopSmall = this.parent.getYSize() != 166;
        this.posX = posX;
        this.posY = this.isTopSmall ? posY - 3 : posY;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        Minecraft.getInstance().getTextureManager().bindTexture(CLOSE_BUTTON);
        this.x = this.parent.getGuiLeft() + this.parent.getXSize() - this.posX - this.width;
        this.y = this.parent.getGuiTop() + this.posY;
        if (this.isTopSmall) {

            this.blit(matrixStack, this.x + 1, this.y + 1, 1, this.isHovered() ? this.height + 1 : 1, this.width - 2, this.height - 2);
        } else {

            this.blit(matrixStack, this.x, this.y, 0, this.isHovered() ? this.height : 0, this.width, this.height);
        }
    }

}
