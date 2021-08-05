package fuzs.consoleexperience.client.gui.screen;

import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyDownloadTerrainScreen extends DownloadTerrainScreen {

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      FancyScreenUtil.renderPanorama();
      FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);
      FancyScreenUtil.drawCenteredString(matrixStack, this.font, new TranslationTextComponent("multiplayer.downloadingTerrain"), this.width, this.height);
      FancyScreenUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

}