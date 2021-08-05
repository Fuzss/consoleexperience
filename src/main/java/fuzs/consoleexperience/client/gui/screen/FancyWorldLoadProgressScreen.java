package fuzs.consoleexperience.client.gui.screen;

import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyWorldLoadProgressScreen extends WorldLoadProgressScreen {

   private final TrackingChunkStatusListener tracker;
   private long lastNarratorUpdateTime = -1L;

   public FancyWorldLoadProgressScreen(TrackingChunkStatusListener tracker) {

      super(tracker);
      this.tracker = tracker;
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      FancyScreenUtil.renderPanorama();
      FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height);

      // narrator stuff
      String progressString = MathHelper.clamp(this.tracker.getProgress(), 0, 100) + "%";
      long milliTime = Util.getMillis();
      if (milliTime - this.lastNarratorUpdateTime > 2000L) {

         this.lastNarratorUpdateTime = milliTime;
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.loading", progressString)).getString());
      }

      FancyScreenUtil.drawCenteredString(matrixStack, this.font, new TranslationTextComponent("menu.loadingLevel"), this.width, this.height);
      FancyScreenUtil.renderLoadingBar(matrixStack, this.font, new TranslationTextComponent("menu.generatingTerrain"), this.width, this.height, this.tracker.getProgress());
      FancyScreenUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30);

      // manual super call
      for (Widget button : this.buttons) {

         button.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

}