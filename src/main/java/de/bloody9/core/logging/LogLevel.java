package de.bloody9.core.logging;

public enum  LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    LOG;

    public static boolean contains(String test) {

        for (LogLevel level : LogLevel.values()) {
            if (level.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
