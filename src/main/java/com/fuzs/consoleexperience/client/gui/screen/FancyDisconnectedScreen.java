package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyDisconnectedScreen extends DisconnectedScreen {

   private final RenderSkybox panorama;
   private final ITextComponent message;
   private IBidiRenderer messageRenderer = IBidiRenderer.field_243257_a;
   private final Screen nextScreen;

   public FancyDisconnectedScreen(Screen nextScreen, ITextComponent title, ITextComponent message) {

      super(nextScreen, title, message);
      this.panorama = FancyScreenUtil.getPanorama();
      this.nextScreen = nextScreen;
      this.message = message;
   }

   @Override
   protected void init() {

      this.messageRenderer = IBidiRenderer.func_243258_a(this.font, this.message, this.width - 50);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, new TranslationTextComponent("gui.toMenu"), (p_213033_1_) -> {

         assert this.minecraft != null;
         this.minecraft.displayGuiScreen(this.nextScreen);
      }));
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      this.panorama.render(partialTicks, 1.0F);
      FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);
      FancyScreenUtil.drawCenteredString(matrixStack, this.font, this.title, this.width, this.height);
      this.messageRenderer.func_241863_a(matrixStack, this.width / 2, this.height / 4 + 86);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }
}