package com.fuzs.consoleexperience.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class StringListBuilder<T extends IForgeRegistryEntry<T>> extends StringListParser<T> {

    public StringListBuilder(IForgeRegistry<T> registry, Logger logger) {

        super(registry, logger);
    }

    public Set<T> buildEntrySet(List<String> locations) {

        return this.buildEntrySetWithCondition(locations, flag -> true, "");
    }

    public Set<T> buildEntrySetWithCondition(List<String> locations, Predicate<T> condition, String message) {

        Set<T> set = Sets.newHashSet();
        for (String source : locations) {

            this.parseResourceLocation(source).flatMap(this::getEntryFromRegistry).ifPresent(entry -> {

                if (condition.test(entry)) {

                    set.add(entry);
                } else {

                    this.logStringParsingError(source, message);
                }
            });
        }

        return set;
    }

    public Map<T, Double> buildEntryMap(List<String> locations) {

        return this.buildEntryMapWithCondition(locations, (entry, value) -> true, "");
    }

    public Map<T, Double> buildEntryMapWithCondition(List<String> locations, BiPredicate<T, Double> condition, String message) {

        Map<T, Double> map = Maps.newHashMap();
        for (String source : locations) {

            String[] s = source.split(",");
            if (s.length == 2) {

                Optional<T> entry = this.getEntryFromRegistry(s[0]);
                Optional<Double> size = Optional.empty();
                try {

                    size = Optional.of(Double.parseDouble(s[1]));
                } catch (NumberFormatException e) {

                    this.logStringParsingError(source, "Invalid number format");
                }

                if (entry.isPresent() && size.isPresent()) {

                    if (condition.test(entry.get(), size.get())) {

                        map.put(entry.get(), size.get());
                    } else {

                        this.logStringParsingError(source, message);
                    }
                }
            } else {

                this.logStringParsingError(source, "Insufficient number of arguments");
            }
        }

        return map;
    }

}
