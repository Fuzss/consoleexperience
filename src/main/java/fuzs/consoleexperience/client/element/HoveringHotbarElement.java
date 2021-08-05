package fuzs.consoleexperience.client.element;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.util.CompatibilityMode;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;

@SuppressWarnings("deprecation")
public class HoveringHotbarElement extends GameplayElement implements IHasDisplayTime {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    // list of gui elements to be moved, idea is to basically wrap around them and whatever other mods would be doing
    private static final List<RenderGameOverlayEvent.ElementType> SHIFTED_ELEMENTS = Lists.newArrayList(
            ElementType.ARMOR, ElementType.HEALTH, ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR,
            ElementType.EXPERIENCE, ElementType.HEALTHMOUNT, ElementType.JUMPBAR
    );
    
    private int xOffset;
    private int yOffset;
    private boolean moveChat;
    private boolean moveXpLevel;
    private CompatibilityMode compatibilityMode;

    private boolean visible;

    @Override
    public void setup() {

        this.addListener(this::onRenderGameOverlayPre1, EventPriority.HIGHEST, true);
        this.addListener(this::onRenderGameOverlayPre2, EventPriority.LOWEST, true);
        this.addListener(this::onRenderGameOverlayPost1, EventPriority.HIGHEST);
        this.addListener(this::onRenderGameOverlayPost2, EventPriority.LOWEST);
        this.addListener(this::onRenderGameOverlayPostHotbar);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Hovering Hotbar";
    }

    @Override
    public String getDescription() {

        return "Enable the hotbar to hover anywhere on the screen. By default just moves it up a little from the screen bottom.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from screen bottom.").defineInRange("Y-Offset", 18, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Move chat together with hotbar on the vertical axis.").define("Move Chat", true), v -> this.moveChat = v);
        registerClientEntry(builder.comment("Show xp level display a few pixels above the bar.").define("Move Xp Level", true), v -> this.moveXpLevel = v);
        registerClientEntry(builder.comment("Compatibility mode for screen elements from mods that normally go unaffected. May have an unwanted impact on other elements, too. Tinker around with modes 1-3, setting to 0 will disable this mode.").define("Compatibility Mode", 0), v -> this.compatibilityMode = MathHelper.clamp(v, 0, 3) == v ? CompatibilityMode.values()[v] : CompatibilityMode.NONE);
    }

    @Override
    public boolean isVisible() {

        return this.visible;
    }

    private void onRenderGameOverlayPre1(final RenderGameOverlayEvent.Pre evt) {

        if (SHIFTED_ELEMENTS.contains(evt.getType()) || (CompatibilityMode.isEnabled(CompatibilityMode.PRE, this.compatibilityMode) && evt.getType() == ElementType.ALL)) {

            this.enable();
        } else if (this.moveChat && evt.getType() == ElementType.CHAT) {

            this.visible = true;
            RenderSystem.translatef(0.0F, -this.getChatOffset(), 0.0F);
        }
    }

    private void onRenderGameOverlayPre2(final RenderGameOverlayEvent.Pre evt) {

        if (CompatibilityMode.isEnabled(CompatibilityMode.PRE, this.compatibilityMode) && evt.getType() == ElementType.ALL) {

            this.disable();
        } else if (evt.isCanceled()) {

            if (SHIFTED_ELEMENTS.contains(evt.getType())) {

                this.disable();
            } else if (this.moveChat && evt.getType() == ElementType.CHAT) {

                this.visible = false;
                RenderSystem.translatef(0.0F, this.getChatOffset(), 0.0F);
            }
        }
    }

    private void onRenderGameOverlayPost1(final RenderGameOverlayEvent.Post evt) {

        if (CompatibilityMode.isEnabled(CompatibilityMode.POST, this.compatibilityMode) && evt.getType() == ElementType.ALL) {

            this.enable();
        }
    }

    private void onRenderGameOverlayPost2(final RenderGameOverlayEvent.Post evt) {

        if (SHIFTED_ELEMENTS.contains(evt.getType()) || CompatibilityMode.isEnabled(CompatibilityMode.POST, this.compatibilityMode) && evt.getType() == ElementType.ALL) {

            this.disable();
        } else if (this.moveChat && evt.getType() == ElementType.CHAT) {

            this.visible = false;
            RenderSystem.translatef(0.0F, this.getChatOffset(), 0.0F);
        }
    }

    private void onRenderGameOverlayPostHotbar(final RenderGameOverlayEvent.Post evt) {

        if (evt.getType() == ElementType.HOTBAR) {

            this.redrawSelectedSlot(evt.getMatrixStack(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        }
    }

    private void enable() {

        if (!this.isVisible()) {

            RenderSystem.translatef(this.xOffset, -this.yOffset, 0.0F);
            this.visible = true;
        } else {

            ConsoleExperience.LOGGER.error("Overflow trying to shift hotbar");
        }
    }

    private void disable() {

        if (this.isVisible()) {

            RenderSystem.translatef(-this.xOffset, this.yOffset, 0.0F);
            this.visible = false;
        } else {

            ConsoleExperience.LOGGER.error("Underflow trying to shift hotbar");
        }
    }

    private int getChatOffset() {

        return this.yOffset < this.mc.getWindow().getGuiScaledHeight() / 3 ? this.yOffset : 0;
    }

    public int getTooltipOffset() {

        return this.isEnabled() && this.moveChat ? this.getChatOffset() : 0;
    }

    public boolean moveXpLevel() {

        return this.isEnabled() && this.moveXpLevel;
    }

    public int getXOffset() {

        return this.isEnabled() ? this.xOffset : 0;
    }

    public int getYOffset() {

        return this.isEnabled() ? this.yOffset : 0;
    }

    public void run(Runnable runnable) {

        if (this.isEnabled() && this.isVisible()) {

            this.disable();
            runnable.run();
            this.enable();
        } else {

            runnable.run();
        }
    }

    /**
     * draw current item highlight again as it's missing two rows of pixels normally
     */
    private void redrawSelectedSlot(MatrixStack matrixStack, int width, int height) {
        
        assert this.mc.player != null;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bind(WIDGETS);
        RenderSystem.enableBlend();
        AbstractGui.blit(matrixStack, width / 2 - 91 - 1 + this.mc.player.inventory.selected * 20, height - 1, 0, 44, 24, 2, 256, 256);
        RenderSystem.disableBlend();
    }

}
