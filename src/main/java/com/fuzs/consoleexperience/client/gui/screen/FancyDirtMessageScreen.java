package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.element.FancyMenusElement;
import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.client.gui.screen.util.RenderTooltipUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FancyDirtMessageScreen extends DirtMessageScreen {

   private final RenderSkybox panorama;
   private IBidiRenderer randomMessage = IBidiRenderer.field_243257_a;

   public FancyDirtMessageScreen(ITextComponent p_i51114_1_) {

      super(p_i51114_1_);
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
      FancyScreenUtil.drawCenteredString(matrixStack, this.font, this.title, this.width, this.height);
      RenderTooltipUtil.drawTooltip(matrixStack, this.width / 2, this.height / 2 + 70, 280, 30, this.randomMessage);
   }

}