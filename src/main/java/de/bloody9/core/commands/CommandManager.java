package de.bloody9.core.commands;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.exceptions.Command.BotCommandException;
import de.bloody9.core.models.interfaces.BotCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        info("user: " + sender.getAsTag() + ", executing command:" + message.getContentDisplay());

        debug("command:" + command);
        debug("arguments: " + Arrays.toString(args));

        boolean success = true;
        boolean removeMsg = true;
        if (command.equals("")) {
            debug("command is empty");
            removeMsg = getBotCommand("help").performCommand(null, sender, null, null);
        } else {
            debug("command is: " + command);
            BotCommand cmd = getBotCommand(command);
            if (cmd != null) {
                debug("BotCommand found");
                try {
                    debug("performing command");
                    removeMsg = cmd.performCommand(command, sender, message, args);
                    debug("performing done");
                } catch (BotCommandException failedExecution) {
                    warn("Command:" + command);
                    warn("Failed to execute command because: " + failedExecution.getMessage());
                    success = false;
                } catch (Exception ex) {
                    error(ex);
                    Helper.sendPrivateMessage(sender, "An error occurred while executing your command: " + ex.toString());
                    success = false;
                }
            } else {
                debug("ServerCommand not found performCommand help");
                Helper.sendPrivateMessage(sender, "Command not found:\n" + message.getContentDisplay());
                removeMsg = getBotCommand("help").performCommand(null, sender, null, null);
            }
        }

        if (removeMsg) {
            debug("delete initial command message on discord");
            message.delete().delay(1, TimeUnit.SECONDS).queue();
        }

        debug("result of command:" + success);
        return success;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }

    public BotCommand getBotCommand(@NotNull String command) {
        String searchCmd = command.toLowerCase();
        if (commands.containsKey(searchCmd)) {
            return commands.get(searchCmd);
        }

        return this.commands.values().stream().filter(cmd -> cmd.getAlias().contains(searchCmd)).findFirst().orElse(null);
    }

    public boolean addBotCommand(@NotNull String name, @NotNull BotCommand command) {
        if (commands.containsKey(name)) {
            return false;
        }
        commands.put(name, command);
        return true;
    }

}
