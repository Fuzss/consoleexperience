package fuzs.consoleexperience.client.gui.screen;

import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import fuzs.consoleexperience.mixin.client.accessor.ConnectingScreenAccessor;
import fuzs.consoleexperience.mixin.client.accessor.ScreenAccessor;
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
        long time = Util.getMillis();
        if (time - lastRenderTime > 2000L) {

            lastRenderTime = time;
            NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.joining")).getString());
        }

        FancyScreenUtil.renderPanorama(partialTicks);
        FancyScreenUtil.renderMenuElements(minecraft, matrixStack, width, height);
        FancyScreenUtil.drawCenteredString(matrixStack, Minecraft.getInstance().font, ((ConnectingScreenAccessor) connectingScreen).getStatus(), width, height);

        // manual super call
        for (Widget button : ((ScreenAccessor) connectingScreen).getButtons()) {

            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

}
