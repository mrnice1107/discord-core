package de.bloody9.core.commands.bot;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;


public class ActivityCommand implements BotCommand {

    private static final String generalPermission = "commands.activity";

    private final List<PermissionObject> permissionObjects;
    private final String description;

    private final String help;

    public ActivityCommand() {
        String prefix = Bot.INSTANCE.getCommandPrefix();
        permissionObjects = new ArrayList<>();

        permissionObjects.add(new PermissionObject(generalPermission, "User is allowed to change the activity of the bot"));

        description = "With this command you can change the activity of your bot!";

        help = "Activity Command\n"
                + prefix + " activity watching/listening/playing <message>\n";
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, help);
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

        debug("start guildCommand");

        debug("command: " + command);
        debug("sender: " + sender.getName() + ":" + sender.getId());
        debug("message: " + message.getContentRaw());
        debug("args: " + Arrays.toString(args));

        if (!memberHasPermission(generalPermission, message.getMember())) {
            return false;
        }

        debug("checking if args.length is 2 or bigger");
        if (args.length < 2) {
            debug("to less arguments");
            sendHelp(sender);
            return false;
        }

        final CharSequence separator = " ";
        final StringJoiner joined = new StringJoiner(separator);

        for (int i = 1; i < args.length; i++) {
            joined.add(args[i]);
        }

        String msg = joined.toString();
        if (msg.length() > 128) {
            Helper.sendPrivateMessage(sender, "The cant be longer then 128 characters");
            return false;
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
                return false;
            }
        }


        Bot.INSTANCE.setActivity(activity);
        Helper.sendPrivateMessage(sender,"You set activity to: " + Helper.getActivityAsString(activity));
        return true;
    }

}