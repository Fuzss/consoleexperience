package fuzs.consolehud.renders;

import com.google.common.collect.Lists;
import fuzs.consolehud.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RenderSelectedItem extends GuiIngame {

    public RenderSelectedItem(Minecraft mcIn) {
        super(mcIn);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.mc.isGamePaused() || event.phase != TickEvent.Phase.END || !ConfigHandler.heldItemTooltips)
            return;

        if (this.mc.thePlayer != null)
        {
            ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

            if (itemstack == null)
            {
                this.remainingHighlightTicks = 0;
            }
            else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            }
            else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.heldItemTooltips) {
            mc.gameSettings.heldItemTooltips = false;
        } else if (!ConfigHandler.heldItemTooltips) {
            mc.gameSettings.heldItemTooltips = true;
        }

        if (this.mc.playerController.isSpectator() || !ConfigHandler.heldItemTooltips) {
            return;
        }

        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
        {
            String s = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName())
            {
                s = TextFormatting.ITALIC + s;
            }

            int i = event.getResolution().getScaledWidth() / 2;
            int j = event.getResolution().getScaledHeight() - 59;

            if (!this.mc.playerController.shouldDrawHUD())
            {
                j += 14;
            }

            int k = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255)
            {
                k = 255;
            }

            if (k > 0)
            {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                List<String> textLines = getToolTipColour(this.highlightingItemStack);
                int listsize = textLines.size();

                if (listsize > ConfigHandler.heldItemTooltipsRows) {
                    listsize = ConfigHandler.heldItemTooltipsRows;
                }
                if (listsize > 2) {
                    this.setRecordPlaying(new TextComponentString(""), false);
                }
                j -= listsize > 1 ? (listsize - 1) * 10 + 2 : (listsize - 1) * 10;

                for (int k1 = 0; k1 < listsize; ++k1)
                {
                    drawCenteredString(textLines.get(k1), (float) i, (float) j, k << 24);

                    if (k1 == 0)
                    {
                        j += 2;
                    }

                    j += 10;
                }
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Removes empty lines from a list of strings
     */
    private List<String> removeEmptyLines(List<String> list) {
        for (int k1 = 0; k1 < list.size(); ++k1)
        {
            if (list.get(k1).isEmpty()) {
                list.remove(k1);
            }
        }
        return list;
    }

    /**
     * Colours first line in a list of strings according to its rarity, other lines that don't have a colour assigned
     * will be coloured grey
     */
    private List<String> getToolTipColour(ItemStack stack) {
        List<String> list = removeEmptyLines(getTooltip(this.mc.thePlayer, stack));

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else if (i == ConfigHandler.heldItemTooltipsRows - 1 && list.size() > ConfigHandler.heldItemTooltipsRows) {
                list.set(i, TextFormatting.GRAY + "..." + TextFormatting.RESET);
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i) + TextFormatting.RESET);
            }
        }

        return list;
    }

    /**
     * Returns the contents of the textbox as float
     */
    private int getShulkerBoxExcess(String line) {
        line = line.replaceAll("[^0-9]","");
        if (line.isEmpty()) {
            line = "0";
        }
        return Integer.valueOf(line);
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    private void drawCenteredString(String text, float x, float y, int color)
    {
        this.getFontRenderer().drawStringWithShadow(text, (x - this.getFontRenderer().getStringWidth(text) / 2), y, color);
    }

    /**
     * Return a list of strings containing information about the item
     */
    @SideOnly(Side.CLIENT)
    private List<String> getTooltip(@Nullable EntityPlayer playerIn, ItemStack stack)
    {
        List<String> list = Lists.<String>newArrayList();
        String s = stack.getDisplayName();

        if (stack.hasDisplayName())
        {
            s = TextFormatting.ITALIC + s;
        }

        if (!stack.hasDisplayName() && stack.getItem() == Items.FILLED_MAP)
        {
            s = s + " #" + stack.getItemDamage();
        }

        s = s + TextFormatting.RESET;

        list.add(s);

        stack.getItem().addInformation(stack, playerIn, list, false);

        if (stack.hasTagCompound())
        {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for (int j = 0; j < nbttaglist.tagCount(); ++j)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                int k = nbttagcompound.getShort("id");
                int l = nbttagcompound.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                if (enchantment != null)
                {
                    list.add(enchantment.getTranslatedName(l));
                }
            }

            if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("display", 10))
            {
                NBTTagCompound nbttagcompound1 = stack.getTagCompound().getCompoundTag("display");

                if (nbttagcompound1.hasKey("color", 3))
                {
                    list.add(TextFormatting.ITALIC + new TextComponentTranslation("item.dyed").getFormattedText());
                }

                if (nbttagcompound1.getTagId("Lore") == 9)
                {
                    NBTTagList nbttaglist3 = nbttagcompound1.getTagList("Lore", 8);

                    if (!nbttaglist3.hasNoTags())
                    {
                        for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1)
                        {
                            list.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + nbttaglist3.getStringTagAt(l1));
                        }
                    }
                }
            }
        }
        if (ConfigHandler.heldItemTooltipsModded) {
            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(stack, playerIn, list, false);
        }
        return list;
    }
}
