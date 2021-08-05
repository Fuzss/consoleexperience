package fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConnectingScreen.class)
public interface ConnectingScreenAccessor {

    @Accessor
    ITextComponent getStatus();

}
