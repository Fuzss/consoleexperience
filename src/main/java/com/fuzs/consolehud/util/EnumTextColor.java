package com.fuzs.consolehud.util;

import net.minecraft.util.text.TextFormatting;

/**
 * Resembles {@link net.minecraft.item.EnumDyeColor} with accessible text formatting codes and a default element
 */
@SuppressWarnings("unused")
public enum EnumTextColor {

    WHITE("white", TextFormatting.WHITE),
    ORANGE("orange", TextFormatting.GOLD),
    MAGENTA("magenta", TextFormatting.AQUA),
    LIGHT_BLUE("light_blue", TextFormatting.BLUE),
    YELLOW("yellow", TextFormatting.YELLOW),
    LIME("lime", TextFormatting.GREEN),
    PINK("pink", TextFormatting.LIGHT_PURPLE),
    GRAY("gray", TextFormatting.DARK_GRAY),
    SILVER("silver", TextFormatting.GRAY),
    CYAN("cyan", TextFormatting.DARK_AQUA),
    PURPLE("purple", TextFormatting.DARK_PURPLE),
    BLUE("blue", TextFormatting.DARK_BLUE),
    BROWN("brown", TextFormatting.RED),
    GREEN("green", TextFormatting.DARK_GREEN),
    RED("red", TextFormatting.DARK_RED),
    BLACK("black", TextFormatting.BLACK);

    private final String unlocalizedName;
    private final TextFormatting chatColor;

    EnumTextColor(String unlocalizedNameIn, TextFormatting chatColorIn) {
        this.unlocalizedName =unlocalizedNameIn;
        this.chatColor = chatColorIn;
    }

    public String toString()
    {
        return this.unlocalizedName;
    }

    public TextFormatting getChatColor()
    {
        return this.chatColor;
    }

}
