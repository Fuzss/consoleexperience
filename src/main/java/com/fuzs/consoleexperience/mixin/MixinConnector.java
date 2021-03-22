package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.ConsoleExperience;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("META-INF/" + ConsoleExperience.MODID + ".mixins.json");
    }

}
