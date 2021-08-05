package fuzs.consoleexperience.client.util;

public enum CompatibilityMode {

    NONE, PRE, POST, BOTH;

    public static boolean isEnabled(CompatibilityMode mode, CompatibilityMode setting) {

        return mode == setting || setting == BOTH;
    }

}
