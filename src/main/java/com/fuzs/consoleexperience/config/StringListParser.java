package com.fuzs.consoleexperience.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class StringListParser<T extends IForgeRegistryEntry<T>> {

    private final IForgeRegistry<T> activeRegistry;
    private final Logger logger;
    
    protected StringListParser(IForgeRegistry<T> registry, Logger logger) {
        
        this.activeRegistry = registry;
        this.logger = logger;
    }

    protected void logStringParsingError(String entry, String message) {
        
        this.logger.error("Unable to parse entry \"" + entry + "\": " + message);
    }

    protected Optional<ResourceLocation> parseResourceLocation(String source) {

        String[] s = source.split(":");
        Optional<ResourceLocation> location = Optional.empty();
        if (s.length == 1) {

            location = Optional.of(new ResourceLocation(s[0]));
        } else if (s.length == 2) {

            location = Optional.of(new ResourceLocation(s[0], s[1]));
        } else {

            this.logStringParsingError(source, "Insufficient number of arguments");
        }

        return location;
    }

    protected Optional<T> getEntryFromRegistry(ResourceLocation location) {

        T entry = this.activeRegistry.getValue(location);
        if (entry != null && entry != this.activeRegistry.getValue(this.activeRegistry.getDefaultKey())) {

            return Optional.of(entry);
        } else {

            this.logStringParsingError(location.toString(), "Item not found");
        }

        return Optional.empty();
    }

    protected Optional<T> getEntryFromRegistry(String source) {

        Optional<ResourceLocation> location = this.parseResourceLocation(source);
        return location.isPresent() ? this.getEntryFromRegistry(location.get()) : Optional.empty();
    }
    
}