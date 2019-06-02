package com.fuzs.consolehud.util;

@SuppressWarnings("unused")
public enum EnumPositionPreset {

    TOP_LEFT("topleft", 0, 0, false, false),
    TOP_RIGHT("topright", 1, 0, true, true),
    BOTTOM_LEFT("bottomleft", 0, 1, false, false),
    BOTTOM_RIGHT("bottomright", 1, 1, true, false);

    private String unlocalizedName;
    private int x;
    private int y;
    private boolean mirror;
    private boolean shift;

    EnumPositionPreset(String unlocalizedNameIn, int x, int y, boolean mirror, boolean potionShift) {
        this.unlocalizedName =unlocalizedNameIn;
        this.x = x;
        this.y = y;
        this.mirror = mirror;
        this.shift = potionShift;
    }

    public String toString() {
        return this.unlocalizedName;
    }

    public boolean isMirrored() {
        return this.mirror;
    }

    public boolean shouldShift() {
        return shift;
    }

    public int getX(int textureWidth, int scaledWidth, int offset) {
        return Math.abs((scaledWidth - textureWidth) * this.x - offset);
    }

    public int getY(int textureHeight, int scaledHeight, int offset) {
        return Math.abs((scaledHeight - textureHeight) * this.y - offset);
    }
}
