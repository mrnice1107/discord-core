package de.bloody9.core.commands;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static de.bloody9.core.logging.Logger.*;

public class CommandManager {
    private final Map<String, BotCommand> commands;

    public CommandManager(Map<String, BotCommand> commands) {
        debug("init");

        this.commands = commands;

        debug("added all commands to command list");
        debug("init done");
    }

    public boolean performCommand(String command, User sender, Message message, String[] args) {
        info("user: " + sender.getName() + ":" + sender.getId() + ", executing command:" + message.getContentDisplay());
        boolean result;
        if (command.equals("")) {
            debug("command is empty");
            result = this.commands.get("help").performCommand(null, sender, null, null);
        } else {
            debug("command is: " + command);
            BotCommand cmd = this.commands.get(command.toLowerCase());
            if (cmd != null) {
                debug("ServerCommand found");
                try {
                    debug("performing command");
                    result = cmd.performCommand(command, sender, message, args);
                    debug("performing done");
                } catch (Exception ex) {
                    error(ex);
                    Helper.sendPrivateMessage(sender, "An error occurred while executing ur command: " + ex.toString());
                    result =  false;
                }
            } else {
                debug("ServerCommand not found performCommand help");
                Helper.sendPrivateMessage(sender, "Command not found:\n" + message.getContentDisplay());
                result = this.commands.get("help").performCommand(null, sender, null, null);
            }
        }

        debug("delete initial command message on discord");
        message.delete().delay(1000L, TimeUnit.MILLISECONDS).queue();

        debug("result of command:" + result);
        return result;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }


}
