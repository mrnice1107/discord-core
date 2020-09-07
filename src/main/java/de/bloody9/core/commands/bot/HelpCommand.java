package de.bloody9.core.commands.bot;

import de.bloody9.core.Bot;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

import static de.bloody9.core.logging.Logger.debug;

public class HelpCommand implements BotCommand {

    private final List<PermissionObject> permissionObjects;
    private final String description;
    private final String prefix;

    private final String help;

    public HelpCommand() {
        prefix = Bot.INSTANCE.getCommandPrefix();

        permissionObjects = new ArrayList<>();

        description = "With this command you can get a list of every command and more details to each command";

        help = "Usage of 'Help Command':\n"
                + prefix + " help [commands...]";
    }

    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }


    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {
        debug("start helpCommand");

        StringBuilder builder = new StringBuilder();
        Bot bot = Bot.INSTANCE;

        if (args == null || args.length == 0) {
            builder.append("Here you can see a list for all commands from the (")
                    .append(bot.getJda().getSelfUser().getName())
                    .append(")\nIf you want further information for a specific command use:\n")
                    .append(help)
                    .append("\nCommands:\n");

            bot.getCommandManager().getCommands().keySet().forEach(key -> builder.append(prefix).append(" ").append(key).append("\n"));
        } else {
            builder.append("Here are some details for your commands:");
            for (String arg : args) {
                arg = arg.toLowerCase();
                BotCommand cmd = bot.getCommandManager().getCommands().get(arg);
                builder.append("\n").append(arg).append(": ");
                if (cmd != null) {
                    builder.append("\n").append(cmd.getDescription()).append("\n").append(cmd.getHelp());
                } else {
                    builder.append("This command dose not exist");
                }

            }
        }


        debug("sending help to user: " + builder.toString());

        Helper.sendPrivateMessage(sender, builder.toString());
        return true;
    }
}
