package fuzs.consoleexperience.client.gui.screen;

import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import fuzs.consoleexperience.mixin.client.accessor.WorkingScreenAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyWorkingScreen extends WorkingScreen {

   private final WorkingScreenAccessor workingScreen;

   public FancyWorkingScreen(WorkingScreen workingScreen) {

      this.workingScreen = (WorkingScreenAccessor) workingScreen;
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      if (this.workingScreen.getStop()) {

         assert this.minecraft != null;
         if (!this.minecraft.isConnectedToRealms()) {

            this.minecraft.setScreen(null);
         }

      } else {

         FancyScreenUtil.renderPanorama();
         FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);
         // this renders nothing as the text component is always null, but for some reason the tooltip isn't be drawn when this doesn't happen
         FancyScreenUtil.drawCenteredString(matrixStack, this.font, this.workingScreen.getHeader(), this.width, this.height);
         FancyScreenUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30);

         if (this.workingScreen.getStage() != null && this.workingScreen.getProgress() != 0) {

            FancyScreenUtil.renderLoadingBar(matrixStack, this.font, this.workingScreen.getStage(), this.width, this.height, this.workingScreen.getProgress());
         }
      }

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

}