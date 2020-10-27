package de.bloody9.core.commands.bot;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.BotCommandException;
import de.bloody9.core.exceptions.Command.NoPermissionCommandException;
import de.bloody9.core.exceptions.Command.NotEnoughArgumentCommandException;
import de.bloody9.core.exceptions.Command.WrongArgumentCommandException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class ActivityCommand implements BotCommand {

    private static final String generalPermission = "commands.activity";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;

    public ActivityCommand() {
        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "User is allowed to change the activity of the bot"));

        aliases = new ArrayList<>();

        description = "With this command you can change the activity of your bot!";

        help = "Activity Command\n" +
                "<prefix> activity watching/listening/playing <message>";
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

    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start guildCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        debug("checking if args.length is 2 or bigger");
        if (args.length < 2) {
            debug("to less arguments");
            sendHelp(sender);
            throw new NotEnoughArgumentCommandException(args.length);
        }

        final StringJoiner joined = new StringJoiner(" ");

        for (int i = 1; i < args.length; i++) {
            joined.add(args[i]);
        }

        String msg = joined.toString();
        if (msg.length() > 128) {
            final String err = "Activity message must be less then 128 characters";
            Helper.sendPrivateMessage(sender, err);
            throw new BotCommandException(err);
        }

        Activity activity;
        switch (args[0].toLowerCase()) {
            case "watch":
            case "watching": {
                activity = Activity.watching(msg);
                break;
            }
            case "listen":
            case "listening": {
                activity = Activity.listening(msg);
                break;
            }
            case "play":
            case "playing": {
                activity = Activity.playing(msg);
                break;
            }
            default: {
                sendHelp(sender);
                throw new WrongArgumentCommandException();
            }
        }


        Bot.INSTANCE.setActivity(activity);
        Helper.sendPrivateMessage(sender,"You set activity to: " + Helper.getActivityAsString(activity));
        return true;
    }

}