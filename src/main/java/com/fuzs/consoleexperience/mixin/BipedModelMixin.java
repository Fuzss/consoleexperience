package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BipedModel.class)
public abstract class BipedModelMixin<T extends LivingEntity> extends AgeableModel<T> {

    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;

    public BipedModelMixin() {

        super(RenderType::getEntityCutoutNoCull, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
    }

    @Inject(method = "func_241654_b_(Lnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private void func_241654_b_(T entity, CallbackInfo ci) {

        int inUseCount = entity.getItemInUseCount();
        if (entity.isHandActive() && inUseCount > 0) {

            Hand hand = entity.getActiveHand();
            ItemStack stack = entity.getHeldItem(hand);
            HandSide handSide = entity.getPrimaryHand();
            if ((hand == Hand.MAIN_HAND ? handSide : handSide.opposite()) == HandSide.RIGHT && (stack.getUseAction() == UseAction.EAT || stack.getUseAction() == UseAction.DRINK)) {

                float useRatio = inUseCount / (float) stack.getUseDuration();
                float f = 1.0F - (float) Math.pow(useRatio, 27.0D);
                if (useRatio < 0.8F) {

                    f += MathHelper.abs(MathHelper.cos(inUseCount / 4.0F * (float)Math.PI) * 0.1F);
                }

                this.bipedRightArm.rotateAngleX = f * (this.bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI * 4.0F / 10.0F));
                this.bipedRightArm.rotateAngleY = f * (-(float) Math.PI / 6F);
            }
        }
    }

    @Inject(method = "func_241655_c_(Lnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private void func_241655_c_(T entity, CallbackInfo ci) {

        int inUseCount = entity.getItemInUseCount();
        if (entity.isHandActive() && inUseCount > 0) {

            Hand hand = entity.getActiveHand();
            ItemStack stack = entity.getHeldItem(hand);
            HandSide handSide = entity.getPrimaryHand();
            if ((hand == Hand.MAIN_HAND ? handSide : handSide.opposite()) == HandSide.LEFT && (stack.getUseAction() == UseAction.EAT || stack.getUseAction() == UseAction.DRINK)) {

                float useRatio = inUseCount / (float) stack.getUseDuration();
                float f = 1.0F - (float) Math.pow(useRatio, 27.0D);
                if (useRatio < 0.8F) {

                    f += MathHelper.abs(MathHelper.cos(inUseCount / 4.0F * (float)Math.PI) * 0.1F);
                }

                this.bipedLeftArm.rotateAngleX = f * (this.bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI * 4.0F / 10.0F));
                this.bipedLeftArm.rotateAngleY = f * (float) Math.PI / 6F;
            }
        }
    }

}