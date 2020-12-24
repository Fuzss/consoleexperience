package com.fuzs.consoleexperience.client.gui.screen;

import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.mixin.ScreenAccessorMixin;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FancyDisconnectedScreen extends DisconnectedScreen {

   private final ITextComponent message;
   private List<String> multilineMessage;
   private final Screen nextScreen;

   public FancyDisconnectedScreen(Screen nextScreen, ITextComponent title, ITextComponent message) {

      super(nextScreen, "", message);
      ((ScreenAccessorMixin) this).setTitle(title);
      this.nextScreen = nextScreen;
      this.message = message;
   }

   @Override
   protected void init() {

      this.multilineMessage = this.font.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, new TranslationTextComponent("gui.toMenu").getFormattedText(), (p_213033_1_) -> {

         assert this.minecraft != null;
         this.minecraft.displayGuiScreen(this.nextScreen);
      }));
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {

      FancyScreenUtil.renderPanorama();
      FancyScreenUtil.renderMenuElements(this.minecraft, this.width, this.height);
      FancyScreenUtil.drawCenteredString(this.font, this.title, this.width, this.height);
      if (this.multilineMessage != null) {

         List<String> strings = this.multilineMessage;
         for (int i = this.height / 4 + 86, j = 0, stringsSize = strings.size(); j < stringsSize; i += 9, j++) {

            this.drawCenteredString(this.font, strings.get(j), this.width / 2, i, 16777215);
         }
      }

      // manual super call
      for (Widget button : this.buttons) {

         button.render(mouseX, mouseY, partialTicks);
      }
   }
}