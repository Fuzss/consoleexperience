package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FancyDownloadTerrainScreen extends DownloadTerrainScreen {

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {

      FancyScreenUtil.renderPanorama();
      FancyScreenUtil.renderMenuElements(this.minecraft, this.width, this.height);
      FancyScreenUtil.drawCenteredString(this.font, new TranslationTextComponent("multiplayer.downloadingTerrain"), this.width, this.height);
      assert this.minecraft != null;
      FancyScreenUtil.drawTooltip(this.minecraft, this.width / 2, this.height / 2 + 70, 280, 30);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(mouseX, mouseY, partialTicks);
      }
   }

}