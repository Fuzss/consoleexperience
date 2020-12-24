package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.mixin.WorkingScreenAccessorMixin;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FancyWorkingScreen extends WorkingScreen {

   private final WorkingScreenAccessorMixin workingScreen;

   public FancyWorkingScreen(WorkingScreen workingScreen) {

      this.workingScreen = (WorkingScreenAccessorMixin) workingScreen;
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {

      if (this.workingScreen.getDoneWorking()) {

         assert this.minecraft != null;
         if (!this.minecraft.isConnectedToRealms()) {

            this.minecraft.displayGuiScreen(null);
         }

      } else {

         FancyScreenUtil.renderPanorama();
         FancyScreenUtil.renderMenuElements(this.minecraft, this.width, this.height);
         // this renders nothing as the text component is always null, but for some reason the tooltip isn't be drawn when this doesn't happen
         FancyScreenUtil.drawCenteredString(this.font, new StringTextComponent(""), this.width, this.height);
         assert this.minecraft != null;
         FancyScreenUtil.drawTooltip(this.minecraft, this.width / 2, this.height / 2 + 70, 280, 30);

         if (this.workingScreen.getStage() != null && this.workingScreen.getProgress() != 0) {

            FancyScreenUtil.renderLoadingBar(this.font, new StringTextComponent(""), this.width, this.height, this.workingScreen.getProgress());
         }
      }

      // manual super call
      for (Widget button : this.buttons) {

         button.render(mouseX, mouseY, partialTicks);
      }
   }

}