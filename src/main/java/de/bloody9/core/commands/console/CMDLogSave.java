package de.bloody9.core.commands.console;

import de.bloody9.core.logging.Logger;
import de.bloody9.core.models.interfaces.SimpleCommand;

import static de.bloody9.core.logging.Logger.debug;

public class CMDLogSave implements SimpleCommand {
    @Override
    public boolean perform(String command, String[] args) {
        debug("saving logs manually");
        Logger.reInit();
        return true;
    }
}
