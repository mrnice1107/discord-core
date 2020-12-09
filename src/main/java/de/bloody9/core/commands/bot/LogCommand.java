package de.bloody9.core.commands.bot;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.exceptions.Mentioned.NoMentionedChannelsCommandException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.GuildObject;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;
import static de.bloody9.core.logging.Logger.debug;

public class LogCommand implements BotCommand {

    private final String generalPermission = "commands.log";
    private final String managePermission = "commands.log.manage";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;


    public LogCommand() {
        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute command"));
        permissionObjects.add(new PermissionObject(managePermission, "Change the mod-log chanel"));

        aliases = new ArrayList<>();
        aliases.add("logging");

        description = "With this command you can set the log channel for the Bots log entries!";

        help = "Log Command\n" +
                "<prefix> log | *get current log channel*\n" +
                "<prefix> log <#channel> | *set a new log channel*";
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, getHelp());
    }

    @Override
    public List<String> getAlias() {
        return aliases;
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
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start guildCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        Guild guild = message.getGuild();
        GuildObject guildObject = new GuildObject(guild);

        debug("check args.length > 0");
        if (args.length == 0) {
            return executeGet(guildObject, sender);
        }

        debug("Switch args[0]: " + args[0].toLowerCase());

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        if (!memberHasPermission(managePermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, managePermission);
        }
        return  executeSet(guildObject, sender, message);
    }

    private boolean executeGet(@NotNull GuildObject guildObject, @NotNull User sender) {

        String message = null;
        switch (guildObject.getModLogStatus()) {
            case LOADED: message = "The current mod-log channel is: " + guildObject.getModLogChannel().getAsMention(); break;
            case NOT_CONFIGURED: message = "No mod-log channel configured"; break;
            case DELETED_CHANNEL: message = "The mod-log channel got deleted"; break;
        }

        message += Helper.constructHelp("\nIf you need more information of how to use this command enter **<prefix> log help**");

        Helper.sendPrivateMessage(message, sender);
        return true;
    }

    private boolean executeSet(@NotNull GuildObject guildObject, @NotNull User sender, @NotNull Message message) {
        List<TextChannel> mentionedChannels = message.getMentionedChannels();
        if (mentionedChannels.isEmpty()) {
            Helper.sendPrivateMessage(sender, "You must mention a channel!");
            throw new NoMentionedChannelsCommandException();
        }
        TextChannel textChannel = mentionedChannels.get(0);
        if (guildObject.setModLogChannel(textChannel, sender.getAsMention())) {
            Helper.sendPrivateMessage(sender, "You successfully change the log channel to: " +textChannel.getAsMention());
        } else {
            throw new BotCommandException("Failed to update mod log channel");
        }

        return true;
    }
}
