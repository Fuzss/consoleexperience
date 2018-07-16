package fuzs.consolehud;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RenderSelectedItem extends GuiIngame {
    private boolean isEnabled;

    public RenderSelectedItem(Minecraft mcIn) {
        super(mcIn);
        isEnabled = mc.gameSettings.heldItemTooltips;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.mc.isGamePaused() || event.phase != TickEvent.Phase.END)
            return;

        if (this.mc.player != null && isEnabled)
        {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (itemstack.isEmpty())
            {
                this.remainingHighlightTicks = 0;
            }
            else if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata()))
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
    public void preRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.heldItemTooltips) {
            mc.gameSettings.heldItemTooltips = false;
        }
        if (this.mc.playerController.isSpectator()) {
            return;
        }

        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty())
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
                List<String> textLines = getToolTipColour(this.highlightingItemStack);
                int listsize = textLines.size();

                if (listsize > 5) {
                    listsize = 5;
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
        List<String> list = removeEmptyLines(getTooltip(this.mc.player, stack));

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else if (i == 4 && list.size() > 5) {
                list.set(i, TextFormatting.GRAY + "...");
            } else if (!(list.get(i).charAt(0) == 167)) {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        return list;
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

        s = s + TextFormatting.RESET;

        list.add(s);

        stack.getItem().addInformation(stack, playerIn == null ? null : playerIn.world, list, ITooltipFlag.TooltipFlags.NORMAL);

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
        }
        return list;
    }
}
