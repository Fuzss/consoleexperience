package com.fuzs.consoleexperience.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener {

    public InventoryScreenMixin(PlayerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {

        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/inventory/InventoryScreen;hasActivePotionEffects:Z", shift = At.Shift.AFTER))
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {

        this.hasActivePotionEffects = false;
    }

}
