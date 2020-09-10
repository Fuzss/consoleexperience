package com.fuzs.consoleexperience.client.tooltip;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class TooltipElements {

    public static class Name extends TooltipElementBase.Text {

        public Name(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
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

            ITextComponent iformattabletextcomponent = new StringTextComponent("").appendSibling(itemstack.getDisplayName()).applyTextStyle(itemstack.getRarity().color);
            if (itemstack.hasDisplayName()) {

                iformattabletextcomponent.applyTextStyle(TextFormatting.ITALIC);
            }

            if (GameplayElements.SELECTED_ITEM.isEnabled()) {

                return Collections.singletonList(this.setDefaultableStyle(iformattabletextcomponent));
            }

            return Collections.singletonList(iformattabletextcomponent);
        }

    }

    public static class Information extends TooltipElementBase.Text {

        private ITooltipFlag itooltipflag;

        public Information(boolean enabled, int ordering, int priority, TextFormatting color, ITooltipFlag itooltipflag) {

            super(enabled, ordering, priority, color);
            this.itooltipflag = itooltipflag;
        }

        @Override
        public String getName() {

            return "information";
        }

        @Override
        protected String getComment() {

            return "Additional information supplied by individual items";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            List<ITextComponent> tooltip = Lists.newArrayList();
            if (!this.itooltipflag.isAdvanced() && !itemstack.hasDisplayName() && itemstack.getItem() == Items.FILLED_MAP) {

                tooltip.add(new StringTextComponent("#" + FilledMapItem.getMapId(itemstack)).applyTextStyle(TextFormatting.GRAY));
            }

            if (testHiddenFlags(itemstack, 32)) {

                if (Block.getBlockFromItem(itemstack.getItem()) instanceof ShulkerBoxBlock) {

                    ShulkerTooltipBuilder.addInformation(tooltip, itemstack, -1, false);
                } else {

                    itemstack.getItem().addInformation(itemstack, playerIn != null ? playerIn.world : null, tooltip, this.itooltipflag);
                }
            }

            tooltip.removeIf(component -> Strings.isNullOrEmpty(component.getString()));
            tooltip.forEach(this::setDefaultableStyle);

            return tooltip;
        }

        @Override
        public JsonElement serialize() {

            JsonObject jsonObject = super.serialize().getAsJsonObject();
            serializeTooltipFlag(jsonObject, this.itooltipflag);
            return jsonObject;
        }

        @Override
        public void deserialize(@Nullable JsonElement element) {

            super.deserialize(element);
            if (element != null && element.isJsonObject()) {

                JsonObject jsonobject = JSONUtils.getJsonObject(element, "tooltip_" + this.getName());
                this.itooltipflag = deserializeTooltipFlag(jsonobject, this.itooltipflag);
            }
        }

    }

    public static class Enchantments extends TooltipElementBase.Text {

        public Enchantments(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "enchantments";
        }

        @Override
        protected String getComment() {

            return "Enchantments this item has";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.hasTag() && testHiddenFlags(itemstack, 1)) {

                List<ITextComponent> tooltip = Lists.newArrayList();
                ListNBT listNBT = itemstack.getEnchantmentTagList();
                for(int i = 0; i < listNBT.size(); i++) {

                    CompoundNBT compoundnbt = listNBT.getCompound(i);
                    ResourceLocation enchantmentLocation = ResourceLocation.tryCreate(compoundnbt.getString("id"));
                    Optional<Enchantment> enchantmentEntry = Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(enchantmentLocation));
                    enchantmentEntry.ifPresent(enchantment -> {

                        ITextComponent itextcomponent = enchantment.getDisplayName(compoundnbt.getInt("lvl"));
                        tooltip.add(this.setDefaultableStyle(itextcomponent));
                    });
                }

                return tooltip;
            }

            return Lists.newArrayList();
        }

    }

    public static class Color extends TooltipElementBase.Text {

        private ITooltipFlag itooltipflag;

        public Color(boolean enabled, int ordering, int priority, TextFormatting color, ITooltipFlag itooltipflag) {

            super(enabled, ordering, priority, color);
            this.itooltipflag = itooltipflag;
        }

        @Override
        public String getName() {

            return "color_tag";
        }

        @Override
        protected String getComment() {

            return "Color data for colored items";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.hasTag()) {

                assert itemstack.getTag() != null;
                if (itemstack.getTag().contains("display", 10)) {

                    CompoundNBT compoundnbt = itemstack.getTag().getCompound("display");
                    if (compoundnbt.contains("color", 99)) {

                        ITextComponent iformattabletextcomponent = this.itooltipflag.isAdvanced() ?
                                new TranslationTextComponent("item.color", String.format("#%06X", compoundnbt.getInt("color"))) :
                                new TranslationTextComponent("item.dyed");
                        return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
                    }
                }
            }

            return Lists.newArrayList();
        }

        @Override
        public JsonElement serialize() {

            JsonObject jsonObject = super.serialize().getAsJsonObject();
            serializeTooltipFlag(jsonObject, this.itooltipflag);
            return jsonObject;
        }

        @Override
        public void deserialize(@Nullable JsonElement element) {

            super.deserialize(element);
            if (element != null && element.isJsonObject()) {

                JsonObject jsonobject = JSONUtils.getJsonObject(element, "tooltip_" + this.getName());
                this.itooltipflag = deserializeTooltipFlag(jsonobject, this.itooltipflag);
            }
        }

    }

    public static class Lore extends TooltipElementBase.Text {

        public Lore(boolean enabled, int ordering, int priority, Style style) {

            super(enabled, ordering, priority, style);
        }

        @Override
        public String getName() {

            return "lore_tag";
        }

        @Override
        protected String getComment() {

            return "Custom lore tag";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.hasTag()) {

                assert itemstack.getTag() != null;
                if (itemstack.getTag().contains("display", 10)) {

                    CompoundNBT compoundnbt = itemstack.getTag().getCompound("display");
                    if (compoundnbt.getTagId("Lore") == 9) {

                        ListNBT listnbt = compoundnbt.getList("Lore", 8);
                        for (int i = 0; i < listnbt.size(); i++) {

                            try {

                                ITextComponent iformattabletextcomponent = ITextComponent.Serializer.fromJson(listnbt.getString(i));
                                if (iformattabletextcomponent != null) {

                                    return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
                                }
                            } catch (JsonParseException jsonparseexception) {

                                compoundnbt.remove("Lore");
                            }
                        }
                    }
                }
            }

            return Lists.newArrayList();
        }

    }

    public static class Modifiers extends TooltipElementBase.Text {

        public Modifiers(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "modifiers";
        }

        @Override
        protected String getComment() {

            return "Attributes this item has";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (testHiddenFlags(itemstack, 2)) {

                for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {

                    Multimap<String, AttributeModifier> multimap = itemstack.getAttributeModifiers(equipmentslottype);
                    if (!multimap.isEmpty()) {

                        ITextComponent iformattabletextcomponent = this.setDefaultableStyle(new TranslationTextComponent("item.modifiers." + equipmentslottype.getName()).applyTextStyle(TextFormatting.GRAY));
                        List<ITextComponent> tooltip = Arrays.asList(iformattabletextcomponent);
                        for(Map.Entry<String, AttributeModifier> entry : multimap.entries()) {

                            AttributeModifier attributemodifier = entry.getValue();
                            double amount = attributemodifier.getAmount();
                            boolean isKnownModifier = false;
                            if (playerIn != null) {

                                if (attributemodifier.getID().equals(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"))) {

                                    amount += playerIn.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                                    amount += EnchantmentHelper.getModifierForCreature(itemstack, CreatureAttribute.UNDEFINED);
                                    isKnownModifier = true;
                                } else if (attributemodifier.getID().equals(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"))) {

                                    amount += playerIn.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                                    isKnownModifier = true;
                                }
                            }

                            double adjustedAmount;
                            if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {

                                adjustedAmount = entry.getKey().equals(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName()) ? amount * 10.0D : amount;
                            } else {

                                adjustedAmount = amount * 100.0D;
                            }

                            if (isKnownModifier) {

                                tooltip.add(this.setDefaultableStyle(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(adjustedAmount), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN)));
                            } else if (amount > 0.0D) {

                                tooltip.add(this.setDefaultableStyle(new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(adjustedAmount), new TranslationTextComponent("attribute.name." + entry.getKey())).applyTextStyle(TextFormatting.BLUE)));
                            } else if (amount < 0.0D) {

                                adjustedAmount *= -1.0D;
                                tooltip.add(this.setDefaultableStyle(new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(adjustedAmount), new TranslationTextComponent("attribute.name." + entry.getKey())).applyTextStyle(TextFormatting.RED)));
                            }
                        }

                        return tooltip;
                    }
                }
            }

            return Lists.newArrayList();
        }

    }

    public static class Unbreakable extends TooltipElementBase.Text {

        public Unbreakable(boolean enabled, int ordering, int priority, TextFormatting color) {

            super(enabled, ordering, priority, color);
        }

        @Override
        public String getName() {

            return "unbreakable";
        }

        @Override
        protected String getComment() {

            return "Shown if this item is unbreakable";
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.hasTag() && testHiddenFlags(itemstack, 4)) {

                assert itemstack.getTag() != null;
                if (itemstack.getTag().getBoolean("Unbreakable")) {

                    ITextComponent iformattabletextcomponent = new TranslationTextComponent("item.unbreakable");
                    return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
                }
            }

            return Lists.newArrayList();
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
        protected boolean isAlwaysUpdate() {
            
            return true;
        }

        @Override
        protected List<ITextComponent> build(ItemStack itemstack, @Nullable PlayerEntity playerIn) {

            if (itemstack.isDamaged()) {

                ITextComponent iformattabletextcomponent = new TranslationTextComponent("item.durability", itemstack.getMaxDamage() - itemstack.getDamage(), itemstack.getMaxDamage());
                return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
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
            ITextComponent iformattabletextcomponent = new StringTextComponent(nameID.toString());
            return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
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
                ITextComponent iformattabletextcomponent = new TranslationTextComponent("item.nbt_tags", itemstack.getTag().keySet().size());
                return Collections.singletonList(iformattabletextcomponent.setStyle(this.getStyle()));
            }

            return Lists.newArrayList();
        }

    }

}
