package com.fuzs.consoleexperience.client.tooltip;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class TooltipElements {

    public static class Name extends TooltipElementBase {

        private boolean rarity;

        public Name(boolean enabled, int ordering, int priority, boolean rarity) {

            super(enabled, ordering, priority);
            this.rarity = rarity;
        }

        @Override
        public String getName() {

            return "name";
        }

        @Override
        protected String getComment() {

            return "Name of the item";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("").append(itemstack.getDisplayName());
            if (this.rarity) {

                iformattabletextcomponent.mergeStyle(itemstack.getRarity().color);
            }

            if (itemstack.hasDisplayName()) {

                iformattabletextcomponent.mergeStyle(TextFormatting.ITALIC);
            }

            return Lists.newArrayList(iformattabletextcomponent);
        }

        @Nullable
        @Override
        public JsonElement serialize() {

            JsonObject jsonObject = super.serialize().getAsJsonObject();
            jsonObject.addProperty("rarity", this.rarity);
            return jsonObject;
        }

        @Override
        public void deserialize(@Nullable JsonElement element) {

            super.deserialize(element);
            if (element != null && element.isJsonObject()) {

                JsonObject jsonobject = JSONUtils.getJsonObject(element, "tooltip_" + this.getName());
                this.rarity = JSONUtils.getBoolean(jsonobject, "rarity", this.rarity);
            }
        }

    }

    public static class Durability extends TooltipElementBase.Text {

        public Durability(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "durability";
        }

        @Override
        protected String getComment() {

            return "Durability if the item is damageable and has been used";
        }
        
        @Override
        protected boolean alwaysUpdate() {
            
            return true;
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.isDamaged()) {

                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent("item.durability", itemstack.getMaxDamage() - itemstack.getDamage(), itemstack.getMaxDamage());
                return Lists.newArrayList(iformattabletextcomponent.mergeStyle(this.getStyle()));
            }

            return Lists.newArrayList();
        }

    }

    public static class NameID extends TooltipElementBase.Text {

        public NameID(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "name_id";
        }

        @Override
        protected String getComment() {

            return "Internal id of the item";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            ResourceLocation nameID = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemstack.getItem()));
            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent(nameID.toString());
            return Lists.newArrayList(iformattabletextcomponent.mergeStyle(this.getStyle()));
        }

    }

    public static class NBTAmount extends TooltipElementBase.Text {

        public NBTAmount(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "nbt_amount";
        }

        @Override
        protected String getComment() {

            return "Amount of nbt tags the item has";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.hasTag()) {

                assert itemstack.getTag() != null;
                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent("item.nbt_tags", itemstack.getTag().keySet().size());
                return Lists.newArrayList(iformattabletextcomponent.mergeStyle(this.getStyle()));
            }

            return Lists.newArrayList();
        }

    }

}
