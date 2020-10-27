package de.bloody9.core.commands.bot;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.Bot;
import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;


public class PrefixCommand implements BotCommand {

    private static final String generalPermission = "commands.prefix";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;

    public PrefixCommand() {

        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute PrefixCommand"));

        aliases = new ArrayList<>();

        description = "With this command you can change the prefix of the bot";

        help = "PrefixCommand\n" +
                "<prefix> prefix <new prefix>| changes the prefix\n";
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, getHelp());
    }

    @Override
    public String getHelp() {
        return Helper.constructHelp(help);
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
    public List<String> getAlias() {
        return aliases;
    }

    // if return true the initial command message will be removed
    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start PrefixCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        debug("checking args.length > 0");
        if (args.length <= 0) {
            sendHelp(sender);

            throw new NotEnoughArgumentCommandException(args.length);
        }

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        String newPrefix = args[0];
        Bot INSTANCE = Bot.INSTANCE;
        INSTANCE.setCommandPrefix(newPrefix);

        return true;
    }

}