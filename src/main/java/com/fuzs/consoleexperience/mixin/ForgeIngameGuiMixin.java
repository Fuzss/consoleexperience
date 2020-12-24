package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.HideHudElement;
import com.fuzs.consoleexperience.client.util.CompatibilityMode;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin extends IngameGui {

    public ForgeIngameGuiMixin(Minecraft mcIn) {

        super(mcIn);
    }

    @Inject(method = "renderGameOverlay(F)V", at = @At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;post(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$ElementType;)V", remap = false), cancellable = true)
    public void renderGameOverlay(float partialticks, CallbackInfo ci) {

        HideHudElement hideHudElement = (HideHudElement) GameplayElements.HIDE_HUD;
        if (hideHudElement.isVisible() && CompatibilityMode.isEnabled(CompatibilityMode.POST, hideHudElement.getCompatibilityMode())) {

            ci.cancel();
        }
    }

}
