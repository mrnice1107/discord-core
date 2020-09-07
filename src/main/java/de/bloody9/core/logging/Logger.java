package de.bloody9.core.logging;


import de.bloody9.core.helper.Helper;

import java.util.Arrays;

public class Logger {


    private static LogLevel logLevel = null;

    private static final String prefixError = Colors.ANSI_RED + "[ERROR]";
    private static final String prefixInfo = Colors.ANSI_WHITE + "[INFO]";
    private static final String prefixWarn = Colors.ANSI_CYAN + "[WARN]";
    private static final String prefixDebug = Colors.ANSI_YELLOW + "[DEBUG]";
    private static final String prefixLog = "[LOG]";

    /*
     * -------------
     * Log Functions
     * -------------
     * */


    public static int getLogLevelValue(LogLevel logLevel) {
        if (logLevel == null) {
            return -1;
        }
        switch (logLevel) {
            case DEBUG: return 0;
            case INFO: return 1;
            case ERROR: return 2;
            case LOG: return 3;
            default: return -1;
        }
    }

    public static void log(String message) {
        log(message, 0);
    }
    public static void log(String message, int lowerTrace) {

    }

    public static void debug(String message) {
        debug(message, 0);
    }
    public static void debug(String message, int lowerTrace) {
        write(prefixDebug, message, lowerTrace, LogLevel.DEBUG);
    }

    public static void warn(String message) {
        warn(message, 0);
    }
    public static void warn(String message, int lowerTrace) {
        write(prefixWarn, message, lowerTrace, LogLevel.WARN);
    }

    public static void info(String message) {
        info(message, 0);
    }
    public static void info(String message, int lowerTrace) {
        write(prefixInfo, message, lowerTrace, LogLevel.INFO);
    }

    public static void error(String message) {
        error(message, 0);
    }
    public static void error(String message, int lowerTrace) {
        write(prefixError, message, lowerTrace, LogLevel.ERROR);
    }
    public static void error(Exception exception) {
        if (getLogLevelValue(LogLevel.ERROR) < getLogLevelValue(getLogLevel())) {
            return;
        }

        String toSend = constructMessage("An error occurred!", prefixError, 0);
        System.out.println(toSend);
        exception.printStackTrace();
        log(toSend);
        log(exception.toString());
        log(Arrays.toString(exception.getStackTrace()));
    }

    private static void write(String prefix, String message, int lowerTrace, LogLevel logLevel) {
        if (getLogLevelValue(logLevel) < getLogLevelValue(getLogLevel())) {
            return;
        }

        System.out.println(constructMessage(message, prefix, lowerTrace));

        if (!logLevel.equals(LogLevel.DEBUG)) {
            log(message, lowerTrace);
        }
    }

    private static String constructMessage(String message, String level, int lowerTrace) {
        String path = getTracePath(lowerTrace);

        String time = Helper.getTime();

        StringBuilder logBuilder;
        logBuilder = new StringBuilder();
        logBuilder.append(Colors.ANSI_RESET);
        logBuilder.append(time).append(" "); // time
        logBuilder.append(level).append(" "); // log level
        logBuilder.append("[").append(path).append("] "); // class/method path
        logBuilder.append("-> ").append(message); // Message

        return logBuilder.toString();
    }


    private static String getTracePath(int lowerTrace) {
        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();

        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (!stackTraceElement.getClassName().equals(Logger.class.getName())) {
                if (i + lowerTrace <= stackTrace.length) {
                    return stackTrace[i+lowerTrace].toString();
                }
                return stackTrace[i].toString();
            }
        }
        return stackTrace[stackTrace.length - 1].toString();
    }


    /*
    * -------------
    * Getter/Setter
    * -------------
    * */




    public static LogLevel getLogLevel() {
        if (logLevel == null) {
            logLevel = LogLevel.INFO;
        }
        return Logger.logLevel;
    }

    public static void setLogLevel(LogLevel logLevel) {
        if (logLevel == null) {
            if (Logger.logLevel == null) {
                setLogLevel(LogLevel.INFO);
            }
        } else {
            info("LogLevel changed to: " + logLevel.toString());
            Logger.logLevel = logLevel;
        }
    }

}
