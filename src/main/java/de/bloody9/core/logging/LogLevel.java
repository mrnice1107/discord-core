package de.bloody9.core.logging;

/**
 * Enum of all possible log levels for logger<p>
 * includes:<p>
 *     DEBUG, INFO, WARN, ERROR, LOG, TEST
 *
 */
public enum  LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    LOG,
    TEST;

    public static boolean contains(String test) {

        for (LogLevel level : LogLevel.values()) {
            if (level.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
