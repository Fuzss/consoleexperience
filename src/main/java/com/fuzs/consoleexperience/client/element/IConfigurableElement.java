package com.fuzs.consoleexperience.client.element;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IConfigurableElement {

    boolean isEnabled();

    boolean getDefaultState();

    String getDisplayName();

    String getDescription();

    default void setupConfig(ForgeConfigSpec.Builder builder) {

    }

}
