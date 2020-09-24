package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.RenderTooltipUtil;
import com.fuzs.consoleexperience.client.element.FancyMenusElement;
import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.renderer.RenderSkybox;
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
   private final RenderSkybox panorama;
   private IBidiRenderer randomMessage = IBidiRenderer.field_243257_a;

   public FancyWorldLoadProgressScreen(TrackingChunkStatusListener p_i51113_1_) {

      super(p_i51113_1_);
      this.tracker = p_i51113_1_;
      this.panorama = FancyScreenUtil.getPanorama();
   }

   @Override
   protected void init() {

      this.randomMessage = IBidiRenderer.func_243258_a(this.font, FancyMenusElement.getRandomTip(), 270);
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

      this.panorama.render(partialTicks, 1.0F);
      FancyScreenUtil.renderMenuElements(this.minecraft, matrixStack, this.width, this.height, partialTicks);

      // narrator stuff
      String progressString = MathHelper.clamp(this.tracker.getPercentDone(), 0, 100) + "%";
      long milliTime = Util.milliTime();
      if (milliTime - this.lastNarratorUpdateTime > 2000L) {

         this.lastNarratorUpdateTime = milliTime;
         NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.loading", progressString)).getString());
      }

      FancyScreenUtil.drawCenteredString(matrixStack, this.font, new TranslationTextComponent("menu.loadingLevel"), this.width, this.height);
      FancyScreenUtil.renderLoadingBar(matrixStack, this.font, new TranslationTextComponent("menu.generatingTerrain"), this.width, this.height, this.tracker.getPercentDone() / 100.0F);
      RenderTooltipUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30, this.randomMessage);
   }

}