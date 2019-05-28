package com.fuzs.consolehud.util;

public enum SavePreset {

    TOP_RIGHT(3, 4, false, true),
    BOTTOM_RIGHT(3, 4, false, false),
    TOP_LEFT(3, 4, true, false),
    BOTTOM_LEFT(3, 4, true, false);

    private int x;
    private int y;
    private boolean mirror;
    private boolean shift;

    SavePreset(int x, int y, boolean mirror, boolean shift) {
        this.x = x;
        this.y = y;
        this.mirror = mirror;
        this.shift = shift;
    }

}
