package fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoAccessor {

    @Invoker
    void callMove(double x, double y, double z);

}
