package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.element.FancyMenusElement;
import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.client.gui.screen.util.RenderTooltipUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyDownloadTerrainScreen extends DownloadTerrainScreen {

   private final RenderSkybox panorama;
   private final ITextComponent randomMessage;
   private IBidiRenderer randomRenderer = IBidiRenderer.field_243257_a;

   public FancyDownloadTerrainScreen() {

      this.panorama = FancyScreenUtil.getPanorama();
      this.randomMessage = FancyMenusElement.getRandomTip();
   }

   @Override
   protected void init() {

      this.randomRenderer = IBidiRenderer.func_243258_a(this.font, this.randomMessage, 270);
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      this.panorama.render(partialTicks, 1.0F);
      FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);
      FancyScreenUtil.drawCenteredString(matrixStack, this.font, new TranslationTextComponent("multiplayer.downloadingTerrain"), this.width, this.height);
      RenderTooltipUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30, this.randomRenderer);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

}