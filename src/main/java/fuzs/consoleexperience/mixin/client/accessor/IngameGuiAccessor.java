package fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IngameGui.class)
public interface IngameGuiAccessor {

    @Accessor
    ITextComponent getOverlayMessageString();

    @Accessor
    void setOverlayMessageTime(int overlayMessageTime);

    @Accessor
    int getOverlayMessageTime();

    @Accessor
    boolean getAnimateOverlayMessageColor();

    @Accessor
    void setToolHighlightTimer(int toolHighlightTimer);

    @Accessor
    void setLastToolHighlight(ItemStack lastToolHighlight);

}
