package de.bloody9.core.commands.console;

import de.bloody9.core.Bot;
import de.bloody9.core.models.interfaces.SimpleCommand;

import static de.bloody9.core.logging.Logger.debug;

public class CMDUpdate implements SimpleCommand {
    @Override
    public boolean perform(String command, String[] args) {
        debug("updating config manually");
        Bot.INSTANCE.getUpdater().update();
        return true;
    }
}
