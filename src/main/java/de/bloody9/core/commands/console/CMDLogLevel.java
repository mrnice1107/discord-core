package de.bloody9.core.commands.console;

import de.bloody9.core.logging.LogLevel;
import de.bloody9.core.logging.Logger;
import de.bloody9.core.models.interfaces.SimpleCommand;

import static de.bloody9.core.logging.Logger.debug;

public class CMDLogLevel implements SimpleCommand {
    @Override
    public boolean perform(String command, String[] args) {
        debug("loglevel changing manually");
        if (args.length != 1) {
            debug("incorrect arg length");
            return false;
        }

        try {
            LogLevel logLevel = LogLevel.valueOf(args[0].toUpperCase());
            Logger.setLogLevel(logLevel);
        } catch (IllegalArgumentException e) {
            debug("args[0]: " + args[0] + " wrong argument");
            return false;
        }
        return true;
    }
}
