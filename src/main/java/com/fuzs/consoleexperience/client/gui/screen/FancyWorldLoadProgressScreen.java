package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FancyWorldLoadProgressScreen extends WorldLoadProgressScreen {

   private final TrackingChunkStatusListener tracker;
   private long lastNarratorUpdateTime = -1L;

   public FancyWorldLoadProgressScreen(TrackingChunkStatusListener tracker) {

      super(tracker);
      this.tracker = tracker;
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {

      assert this.minecraft != null;
      FancyScreenUtil.renderPanorama();
      FancyScreenUtil.renderMenuElements(this.minecraft, this.width, this.height);

      // narrator stuff
      String progressString = MathHelper.clamp(this.tracker.getPercentDone(), 0, 100) + "%";
      long milliTime = Util.milliTime();
      if (milliTime - this.lastNarratorUpdateTime > 2000L) {

         this.lastNarratorUpdateTime = milliTime;
         NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.loading", progressString)).getString());
      }

      FancyScreenUtil.drawCenteredString(this.font, new TranslationTextComponent("menu.loadingLevel"), this.width, this.height);
      FancyScreenUtil.renderLoadingBar(this.font, new TranslationTextComponent("menu.generatingTerrain"), this.width, this.height, this.tracker.getPercentDone());
      FancyScreenUtil.drawTooltip(this.minecraft, this.width / 2, this.height / 2 + 70, 280, 30);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(mouseX, mouseY, partialTicks);
      }
   }

}