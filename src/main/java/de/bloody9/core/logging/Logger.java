package de.bloody9.core.logging;


import de.bloody9.core.helper.Helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static int lines;

    private static FileWriter writer;

    private static LogLevel logLevel = null;

    private static final String prefixError = Colors.ANSI_RED + "[ERROR]";
    private static final String prefixInfo = Colors.ANSI_WHITE + "[INFO]";
    private static final String prefixWarn = Colors.ANSI_CYAN + "[WARN]";
    private static final String prefixDebug = Colors.ANSI_YELLOW + "[DEBUG]";
    private static final String prefixLog = "[LOG]";

    private static boolean initialized = false;

    /*
     * -------------
     * Init and other functions
     * -------------
     * */

    public static void init() {
        debug("initialize logfile");
        try {
            String fileName = "log/logging_" + Helper.getTime("yyyy_MM_dd_HH_mm_ss") + ".log";

            File logfile = new File(fileName);
            Helper.createFile(logfile);

            writer = new FileWriter(logfile);
            lines = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialized = true;
    }

    public static void close() {
        if (initialized) {
            try {
                debug("close writer");
                writer.close();
                writer = null;
                initialized = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reInit() {
        debug("reinitialize logfile");
        close();
        init();
    }

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

    private static String constructMessage(CharSequence message, String level, int lowerTrace) {
        return constructMessage(message, level, lowerTrace, true);
    }
    private static String constructMessage(CharSequence message, String level, int lowerTrace, boolean doReset) {
        String path = getTracePath(lowerTrace);

        String time = Helper.getTime();

        StringBuilder logBuilder;
        logBuilder = new StringBuilder();
        if (doReset) logBuilder.append(Colors.ANSI_RESET);
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
     * Log Functions
     * -------------
     * */



    public static void logLine(String fullLogMessage) {
        if (!initialized) {
            init();
        }

        if (lines > 10000) {
            reInit();
        }

        try {
            writer.write(fullLogMessage + "\n");
            lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(CharSequence message, int lowerTrace) { logLine(constructMessage(message, prefixLog, lowerTrace, false)); }
    public static void log(CharSequence message) { log(message, 0); }
    public static void log(Object obj, int lowerTrace) { log(String.valueOf(obj), lowerTrace); }
    public static void log(Object obj) { log(String.valueOf(obj), 0); }

    public static void debug(CharSequence message, int lowerTrace) { write(prefixDebug, message, lowerTrace, LogLevel.DEBUG); }
    public static void debug(CharSequence message) { debug(message, 0); }
    public static void debug(Object obj, int lowerTrace) { debug(String.valueOf(obj), lowerTrace); }
    public static void debug(Object obj) { debug(String.valueOf(obj), 0); }

    public static void warn(CharSequence message, int lowerTrace) { write(prefixWarn, message, lowerTrace, LogLevel.WARN); }
    public static void warn(CharSequence message) { warn(message, 0); }
    public static void warn(Object obj, int lowerTrace) { warn(String.valueOf(obj), lowerTrace); }
    public static void warn(Object obj) { warn(String.valueOf(obj), 0); }

    public static void info(CharSequence message, int lowerTrace) { write(prefixInfo, message, lowerTrace, LogLevel.INFO); }
    public static void info(CharSequence message) { info(message, 0); }
    public static void info(Object obj, int lowerTrace) { info(String.valueOf(obj), lowerTrace); }
    public static void info(Object obj) { info(String.valueOf(obj), 0); }

    public static void error(CharSequence message, int lowerTrace) { write(prefixError, message, lowerTrace, LogLevel.ERROR); }
    public static void error(CharSequence message) { error(message, 0); }
    public static void error(Object obj, int lowerTrace) { error(String.valueOf(obj), lowerTrace); }
    public static void error(Object obj) { error(String.valueOf(obj), 0); }

    public static void error(Exception exception) {
        if (getLogLevelValue(LogLevel.ERROR) < getLogLevelValue(getLogLevel())) {
            return;
        }

        StringBuilder err = new StringBuilder();
        err.append("An error occurred!\n").append(exception.toString());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            err.append("\n").append(stackTraceElement.toString());
        }

        error(err.toString());
    }

    private static void write(String prefix, CharSequence message, int lowerTrace, LogLevel logLevel) {
        if (getLogLevelValue(logLevel) < getLogLevelValue(getLogLevel())) {
            return;
        }
        System.out.println(constructMessage(message, prefix, lowerTrace));

        if (!logLevel.equals(LogLevel.DEBUG)) {
            logLine(constructMessage(message, "[" + logLevel.name() + "]", lowerTrace, false));
        }
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
