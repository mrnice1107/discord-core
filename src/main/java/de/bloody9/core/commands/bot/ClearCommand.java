package de.bloody9.core.commands.bot;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ClearCommand implements BotCommand {

    private static final String generalPermission = "commands.clear";
    private static final String clearAllPermission = "commands.clear.all";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;

    public ClearCommand() {

        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute Clear Command"));
        permissionObjects.add(new PermissionObject(clearAllPermission, "Permission to clear all messages in the channel"));

        aliases = new ArrayList<>();
        aliases.add("clean");

        description = "Clears a certain amount of messages in the executed channel";

        help = "Clear Command\n" +
                "<prefix> clear [<amount>]\n";
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

        debug("start ClearCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        final TextChannel channel = message.getTextChannel();
        final String messageID = message.getId();

        debug("checking args.length > 0");
        if (args.length == 0) {
            if (memberHasPermission(generalPermission, message.getMember())) {
                Helper.clearAllMessagesInChannel(messageID, channel);
                channel.sendMessage("All messages got deleted!").complete().delete().queueAfter(2, TimeUnit.SECONDS);
                return true;
            } else {
                sendHelp(sender);

                throw new NotEnoughArgumentCommandException(args.length);
            }

        }

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        int deleteAmount;

        try {
            deleteAmount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Helper.sendPrivateMessage(sender, "*" + args[0] + "* is no number, this command requires a number");

            throw new NoNumberCommandException();
        }

        Helper.clearAmountOfMessagesInChannel(messageID, channel, deleteAmount);
        channel.sendMessage("*" + deleteAmount + "* messages got deleted!").complete().delete().queueAfter(2, TimeUnit.SECONDS);


        return true;
    }



}