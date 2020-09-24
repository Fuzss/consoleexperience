package com.fuzs.consoleexperience.util;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.config.JSONConfigUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import java.io.File;
import java.io.FileReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandRegisterer {

    public static void handleReload(String jsonName, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer, CommandContext<CommandSource> ctx) {

        JSONConfigUtil.load(jsonName, ConsoleExperience.MODID, serializer, deserializer);
        ctx.getSource().sendFeedback(getFeedbackComponent(jsonName), true);
    }

    private static IFormattableTextComponent getFeedbackComponent(String jsonName) {

        return new TranslationTextComponent("command.reload.config", getClickableComponent(jsonName));
    }

    private static IFormattableTextComponent getClickableComponent(String jsonName) {

        return new StringTextComponent(jsonName).mergeStyle(TextFormatting.UNDERLINE)
                .modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, JSONConfigUtil.getFilePath(jsonName, ConsoleExperience.MODID).getAbsolutePath())));
    }

}
