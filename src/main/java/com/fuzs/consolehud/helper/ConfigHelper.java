package com.fuzs.consolehud.helper;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class ConfigHelper {

    public static String[] getEnumDescription(String comment, Enum<?>[] values) {

        String[] comments = new String[]{comment, "Valid values:"};
        String[] modes = Arrays.stream(values).map(Enum::toString).toArray(String[]::new);
        return ArrayUtils.addAll(comments, modes);

    }

}
