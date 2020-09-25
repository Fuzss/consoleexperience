package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MenuPlayerElement extends GameplayElement {

    private ClientWorld renderWorld;
    private LivingEntity renderEntity;

    @Override
    public void setup() {

        this.addListener(this::onGuiOpen);
        this.addListener(this::onDrawScreen);
        this.addListener(this::onRenderNameplate);
        this.addListener(this::onClientTick);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Menu Player";
    }

    @Override
    public String getDescription() {

        return "Player in da menu!";
    }

    private void onGuiOpen(final GuiOpenEvent evt) {

        if (evt.getGui() instanceof MainMenuScreen) {

            this.setRenderEntityPlayer();
        }
    }

    private void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (evt.getGui() instanceof MainMenuScreen) {

            this.executeCarefully(entity -> {

                int posX = (int) (evt.getGui().width * 6.0F / 7.0F), posY = (int) (evt.getGui().height * 3.0F / 4.0F);
                InventoryScreen.drawEntityOnScreen(posX, posY, 30, -evt.getMouseX() + posX, -evt.getMouseY() + posY - 50, entity);
            });
        }
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END && this.mc.currentScreen instanceof MainMenuScreen) {

            this.executeCarefully(entity -> entity.ticksExisted++);
        }
    }


    private void onRenderNameplate(final RenderNameplateEvent evt) {

        if (evt.getEntity() == this.renderEntity) {

            evt.setResult(Event.Result.DENY);
            this.renderName(evt.getEntity(), evt.getContent(), evt.getMatrixStack(), evt.getRenderTypeBuffer(), evt.getPackedLight(), evt.getEntityRenderer().getRenderManager());
        }
    }

    protected void renderName(Entity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityRendererManager renderManager) {

        float f = entityIn.getHeight() + 0.5F;
        int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, f, 0.0D);
        matrixStackIn.rotate(renderManager.getCameraOrientation());
        matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        FontRenderer fontrenderer = renderManager.getFontRenderer();
        float f2 = (float) (-fontrenderer.func_238414_a_(displayNameIn) / 2);
        fontrenderer.func_243247_a(displayNameIn, f2, (float)i, 553648127, false, matrix4f, bufferIn, true, j, packedLightIn);
        fontrenderer.func_243247_a(displayNameIn, f2, (float)i, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);

        matrixStackIn.pop();
    }

    private ClientWorld getRenderWorld() {

        if (this.renderWorld == null) {

            GameProfile profileIn = this.mc.getSession().getProfile();
            @SuppressWarnings("ConstantConditions")
            ClientPlayNetHandler clientPlayNetHandler = new ClientPlayNetHandler(this.mc, null, null, profileIn);
            ClientWorld.ClientWorldInfo clientworld$clientworldinfo = new ClientWorld.ClientWorldInfo(Difficulty.NORMAL, false, false);
            this.renderWorld = new ClientWorld(clientPlayNetHandler, clientworld$clientworldinfo, World.OVERWORLD, DynamicRegistries.func_239770_b_().func_230520_a_().func_243576_d(DimensionType.OVERWORLD), 3, this.mc::getProfiler, this.mc.worldRenderer, false, 0);
        }

        return this.renderWorld;
    }

    private void setRenderEntityRandom() {

        List<EntityType<?>> entityTypes = ForgeRegistries.ENTITIES.getValues().stream().filter(type -> type.getClassification() != EntityClassification.MISC).collect(Collectors.toList());
        Collections.shuffle(entityTypes);
        Optional<EntityType<?>> optionalLivingEntity = entityTypes.stream().findFirst();
        optionalLivingEntity.ifPresent(type -> this.renderEntity = (LivingEntity) type.create(this.getRenderWorld()));
    }

    private void setRenderEntityPlayer() {

        this.renderEntity = new RemoteClientPlayerEntity(this.getRenderWorld(), this.mc.getSession().getProfile()) {

            @Nullable
            protected NetworkPlayerInfo getPlayerInfo() {

                return null;
            }

            public boolean isSpectator() {

                return false;
            }

        };
    }

    private void executeCarefully(Consumer<LivingEntity> action) {

        try {

            action.accept(this.renderEntity);
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error(this.renderEntity.getType());
            this.setRenderEntityRandom();
        }
    }

}
