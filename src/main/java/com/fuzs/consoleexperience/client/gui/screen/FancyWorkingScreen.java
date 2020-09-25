package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.mixin.WorkingScreenAccessorMixin;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyWorkingScreen extends WorkingScreen {

   private final RenderSkybox panorama;
   private final WorkingScreenAccessorMixin workingScreen;

   public FancyWorkingScreen(WorkingScreen workingScreen) {

      this.panorama = FancyScreenUtil.getPanorama();
      this.workingScreen = (WorkingScreenAccessorMixin) workingScreen;
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      if (this.workingScreen.getDoneWorking()) {

         assert this.minecraft != null;
         if (!this.minecraft.isConnectedToRealms()) {

            this.minecraft.displayGuiScreen(null);
         }

      } else {

         this.panorama.render(0.0F, 1.0F);
         FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);
         if (this.workingScreen.getWorking() != null) {

            FancyScreenUtil.drawCenteredString(matrixStack, this.font, this.workingScreen.getWorking(), this.width, this.height);
         }

         if (this.workingScreen.getStage() != null && this.workingScreen.getProgress() != 0) {

            FancyScreenUtil.renderLoadingBar(matrixStack, this.font, this.workingScreen.getStage(), this.width, this.height, this.workingScreen.getProgress() / 100.0F);
         }
      }

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

}