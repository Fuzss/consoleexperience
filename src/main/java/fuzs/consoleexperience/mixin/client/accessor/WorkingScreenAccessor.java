package fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(WorkingScreen.class)
public interface WorkingScreenAccessor {

    @Nullable
    @Accessor
    ITextComponent getHeader();

    @Nullable
    @Accessor
    ITextComponent getStage();

    @Accessor
    int getProgress();

    @Accessor
    boolean getStop();

}
