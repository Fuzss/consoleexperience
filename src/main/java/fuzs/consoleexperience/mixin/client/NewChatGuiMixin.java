package fuzs.consoleexperience.mixin.client;

import fuzs.consoleexperience.client.element.GameplayElements;
import fuzs.consoleexperience.client.element.HoveringHotbarElement;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.NewChatGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("unused")
@Mixin(NewChatGui.class)
public abstract class NewChatGuiMixin extends AbstractGui {

    @ModifyVariable(method = "getClickedComponentStyleAt", ordinal = 1, at = @At("HEAD"))
    public double getClickedComponentMouseY(double mouseY) {

        // move chat tooltips together with hovering hotbar
        return mouseY + ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getTooltipOffset();
    }

}
