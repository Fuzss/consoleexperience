package fuzs.consoleexperience.client.gui.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class RenderFancySkyboxCube extends RenderSkyboxCube {

    private final ResourceLocation[] locations = new ResourceLocation[6];

    public RenderFancySkyboxCube(ResourceLocation texture) {

        super(texture);
        for(int i = 0; i < 6; ++i) {

            this.locations[i] = new ResourceLocation(texture.getNamespace(), texture.getPath() + '_' + i + ".png");
        }
    }

    @Override
    public void render(Minecraft mc, float pitch, float time, float alpha) {
        float aspectRatio = (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight();
        float imageRatio = 820.0F / 144.0F;
//      System.out.println((float)mc.getMainWindow().getFramebufferWidth() / (float)mc.getMainWindow().getFramebufferHeight());
//      System.out.println(pitch + " " + time + " " + alpha);
//      pitch = 25.0F;
//      time = 0.0F;
//      System.out.println(time);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(Matrix4f.perspective(85.0D, (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight(), 0.05F, 10.0F));
        RenderSystem.matrixMode(5888);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        int i = 2;

        float offsetStart = (time * -0.025F) / imageRatio;
        float offsetTotal = offsetStart + aspectRatio / imageRatio;

        float f15 = 16.0F;

        for (int j = 0; j < 64; ++j) {
            RenderSystem.pushMatrix();
            float f = ((float)(j % 8) / 8.0F - 0.5F) / 32.0F;
            float f1 = ((float)(j / 8) / 8.0F - 0.5F) / 32.0F;

//      for(int j = 0; j < 4; ++j) {
//         RenderSystem.pushMatrix();
//         float f = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
//         float f1 = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
            float f2 = 0.0F;
//         RenderSystem.translatef(f, f1, 0.0F);
            RenderSystem.translatef(f, f1, 0.0F);
//         RenderSystem.rotatef(pitch, 1.0F, 0.0F, 0.0F);
//         RenderSystem.rotatef(time, 0.0F, 1.0F, 0.0F);

            for (int k = 0; k < 1; ++k) {
                mc.getTextureManager().bind(this.locations[k]);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l = Math.round(255.0F * alpha) / (j + 1);
                if (k == 0) {
                    bufferbuilder.vertex(-1.0D * aspectRatio, -1.0D, 1.0D).uv(offsetStart, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D * aspectRatio, 1.0D, 1.0D).uv(offsetStart, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D * aspectRatio, 1.0D, 1.0D).uv(offsetTotal, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D * aspectRatio, -1.0D, 1.0D).uv(offsetTotal, 0.0F).color(255, 255, 255, l).endVertex();
                }

                if (k == 1) {
                    bufferbuilder.vertex(1.0D, -1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, 1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
                }

                if (k == 2) {
                    bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
                }

                if (k == 3) {
                    bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, 1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
                }

                if (k == 4) {
                    bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, -1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
                }

                if (k == 5) {
                    bufferbuilder.vertex(-1.0D, 1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
                    bufferbuilder.vertex(1.0D, 1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
                }

                tessellator.end();
            }

            RenderSystem.popMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }
}
