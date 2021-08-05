package fuzs.consoleexperience.util;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.config.JSONConfigUtil;
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

    public static void handleReload(String jsonName, String translationKey, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer, CommandContext<CommandSource> ctx) {

        JSONConfigUtil.load(jsonName, ConsoleExperience.MODID, serializer, deserializer);
        ctx.getSource().sendSuccess(getFeedbackComponent(jsonName, translationKey), true);
    }

    private static IFormattableTextComponent getFeedbackComponent(String jsonName, String translationKey) {

        return new TranslationTextComponent(translationKey, getClickableComponent(jsonName));
    }

    private static IFormattableTextComponent getClickableComponent(String jsonName) {

        return new StringTextComponent(jsonName).withStyle(TextFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, JSONConfigUtil.getFilePath(jsonName, ConsoleExperience.MODID).getAbsolutePath())));
    }

}
