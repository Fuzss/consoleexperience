package com.fuzs.consoleexperience.client.tooltip;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.SelectedItemElement;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class TooltipElementBase {

    protected List<ITextComponent> fulltip;
    private List<ITextComponent> subtip;
    private boolean enabled;
    private int ordering;
    private int priority;

    protected TooltipElementBase(boolean enabled, int ordering, int priority) {

        this.enabled = enabled;
        this.ordering = ordering;
        this.priority = priority;
    }

    public abstract String getName();

    protected abstract String getComment();

    public final boolean isEnabled() {

        return this.enabled;
    }

    public final int getOrdering() {

        return this.ordering;
    }

    public final int getPriority() {

        return -this.priority;
    }

    public final void reset() {

        this.fulltip = this.subtip = null;
    }

    protected boolean isAlwaysUpdate() {

        return false;
    }

    public void make(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

        if (this.fulltip == null || this.isAlwaysUpdate()) {

            this.fulltip = this.build(itemstack, playerIn);
        }
    }

    public List<ITextComponent> makeAndGet(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

        return this.build(itemstack, playerIn);
    }

    protected abstract List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn);

    public int cut(int amount) {

        assert this.fulltip != null : "Tooltip not made";
        if (this.subtip == null || this.isAlwaysUpdate()) {

            this.subtip = this.fulltip.subList(0, Math.min(this.fulltip.size(), amount));
        }

        return this.fulltip.size();
    }

    @Nonnull
    public List<ITextComponent> get() {

        assert this.fulltip != null : "Tooltip not made";
        return this.subtip != null ? this.subtip : this.fulltip;
    }

    public int size() {

        assert this.fulltip != null : "Tooltip not made";
        return this.fulltip.size();
    }

    @Nullable
    public JsonElement serialize() {

        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("__comment", this.getComment());
        jsonobject.addProperty("enabled", this.enabled);
        jsonobject.addProperty("ordering", this.ordering);
        jsonobject.addProperty("priority", this.priority);
        return jsonobject;
    }

    public void deserialize(@Nullable JsonElement element) {

        if (element != null && element.isJsonObject()) {

            JsonObject jsonobject = JSONUtils.getJsonObject(element, "tooltip_base");
            this.enabled = JSONUtils.getBoolean(jsonobject, "enabled", this.enabled);
            this.ordering = JSONUtils.getInt(jsonobject, "ordering", this.ordering);
            this.priority = JSONUtils.getInt(jsonobject, "priority", this.priority);
        }
    }

    protected static boolean testHiddenFlags(ItemStack itemstack, int flag) {

        int hide = itemstack.hasTag() && Objects.requireNonNull(itemstack.getTag()).contains("HideFlags", 99) ? itemstack.getTag().getInt("HideFlags") : 0;
        return (hide & flag) == 0;
    }

    protected static void serializeTooltipFlag(JsonObject jsonobject, ITooltipFlag flagIn) {

        jsonobject.addProperty("advanced", flagIn.isAdvanced());
    }

    protected static ITooltipFlag deserializeTooltipFlag(JsonObject jsonobject, ITooltipFlag fallback) {

        if (jsonobject.has("advanced")) {

            return JSONUtils.getBoolean(jsonobject, "advanced") ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        } else {

            return fallback;
        }
    }

    public static abstract class Text extends TooltipElementBase {

        private final String name = "formatting";

        @Nullable
        private TextFormatting color;
        private boolean obfuscated;
        private boolean bold;
        private boolean strikethrough;
        private boolean underline;
        private boolean italic;

        protected Text(boolean enabled, int ordering, int priority, Style style) {

            super(enabled, ordering, priority);
            this.color = style.getColor();
            this.obfuscated = style.getObfuscated();
            this.bold = style.getBold();
            this.strikethrough = style.getStrikethrough();
            this.underline = style.getUnderlined();
            this.italic = style.getItalic();
        }

        protected Text(boolean enabled, int ordering, int priority, @Nullable TextFormatting color) {

            super(enabled, ordering, priority);
            // will use default color if null
            this.color = color;
        }

        protected final Style getStyle() {

            return new Style().setColor(this.color != null ? this.color : getDefaultColor())
                    .setObfuscated(this.obfuscated)
                    .setBold(this.bold)
                    .setStrikethrough(this.strikethrough)
                    .setUnderlined(this.underline)
                    .setItalic(this.italic);
        }

        protected final ITextComponent setDefaultableStyle(ITextComponent iformattabletextcomponent) {

            return this.color != null ? iformattabletextcomponent.setStyle(this.getStyle()) : iformattabletextcomponent;
        }

        @Override
        public JsonElement serialize() {

            JsonObject jsonObject = super.serialize().getAsJsonObject();
            JsonObject textFormatting = new JsonObject();
            serializeColor(textFormatting, this.color);
            textFormatting.addProperty("obfuscated", this.obfuscated);
            textFormatting.addProperty("bold", this.bold);
            textFormatting.addProperty("strikethrough", this.strikethrough);
            textFormatting.addProperty("underline", this.underline);
            textFormatting.addProperty("italic", this.italic);
            jsonObject.add(this.name, textFormatting);
            return jsonObject;
        }

        @Override
        public void deserialize(@Nullable JsonElement element) {

            super.deserialize(element);
            if (element != null && element.isJsonObject()) {

                JsonObject jsonobject = JSONUtils.getJsonObject(element, "tooltip_" + this.name);
                JsonObject textFormatting = JSONUtils.getJsonObject(jsonobject, this.name);
                this.color = deserializeColor(textFormatting, this.color);
                this.obfuscated = JSONUtils.getBoolean(textFormatting, "obfuscated", this.obfuscated);
                this.bold = JSONUtils.getBoolean(textFormatting, "bold", this.bold);
                this.strikethrough = JSONUtils.getBoolean(textFormatting, "strikethrough", this.strikethrough);
                this.underline = JSONUtils.getBoolean(textFormatting, "underline", this.underline);
                this.italic = JSONUtils.getBoolean(textFormatting, "italic", this.italic);
            }
        }

        private static TextFormatting getDefaultColor() {

            return ((SelectedItemElement) GameplayElements.SELECTED_ITEM).textColor;
        }

        private static void serializeColor(JsonObject jsonobject, @Nullable TextFormatting color) {

            jsonobject.addProperty("color", color != null ? color.getFriendlyName() : "default");
        }

        @Nullable
        private static TextFormatting deserializeColor(JsonObject jsonobject, TextFormatting fallback) {

            if (jsonobject.has("color")) {

                String s = JSONUtils.getString(jsonobject, "color");
                return TextFormatting.getValueByName(s);
            } else {

                return fallback;
            }
        }

    }

}
