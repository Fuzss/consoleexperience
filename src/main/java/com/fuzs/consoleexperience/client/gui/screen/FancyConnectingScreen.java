package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.mixin.client.accessor.IConnectingScreenAccessor;
import com.fuzs.consoleexperience.mixin.client.accessor.IScreenAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class FancyConnectingScreen {

    private static long lastRenderTime;

    public static void onGuiOpen() {

        lastRenderTime = 0L;
    }
    
    public static void render(Minecraft minecraft, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, ConnectingScreen connectingScreen) {

        int width = connectingScreen.width;
        int height = connectingScreen.height;
        long time = Util.milliTime();
        if (time - lastRenderTime > 2000L) {

            lastRenderTime = time;
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.joining")).getString());
        }

        FancyScreenUtil.renderPanorama();
        FancyScreenUtil.renderMenuElements(minecraft, matrixStack, width, height);
        FancyScreenUtil.drawCenteredString(matrixStack, Minecraft.getInstance().fontRenderer, ((IConnectingScreenAccessor) connectingScreen).getConnectingProgress(), width, height);

        // manual super call
        for (Widget button : ((IScreenAccessor) connectingScreen).getButtons()) {

            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

}
