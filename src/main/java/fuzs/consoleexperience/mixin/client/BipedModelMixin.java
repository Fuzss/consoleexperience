package fuzs.consoleexperience.mixin.client;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BipedModel.class)
public abstract class BipedModelMixin<T extends LivingEntity> extends AgeableModel<T> {

    @Shadow
    public ModelRenderer rightArm;
    @Shadow
    public ModelRenderer leftArm;
    @Shadow
    public boolean crouching;

    @Inject(method = "setupAnim", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;crouching:Z"))
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {

        PlayerAnimationsElement element = (PlayerAnimationsElement) ConsoleExperience.PLAYER_ANIMATIONS;
        if (element.isEnabled() && element.supermanGliding && entityIn.getFallFlyingTicks() > 4 && this.crouching) {

            // superman hand pose
            boolean isRight = entityIn.getMainArm() == HandSide.RIGHT;
            ModelRenderer bipedArm = isRight ? this.rightArm : this.leftArm;
            bipedArm.xRot = (float) Math.PI;
            bipedArm.yRot = 0.0F;
            bipedArm.zRot = 0.0F;

            // disable sneaking pose while gliding
            this.crouching = false;
        }
    }

}