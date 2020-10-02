package de.bloody9.core.commands.bot;

import de.bloody9.core.Bot;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.GuildObject;
import de.bloody9.core.models.objects.PermissionObject;
import de.bloody9.core.mysql.MySQLConnection;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;
import static de.bloody9.core.logging.Logger.debug;

public class LogCommand implements BotCommand {
    private static final String TABLE = "guild_logs";
    private static final String GUILD_ID = "guild_id";
    private static final String LOG_CHANNEL_ID = "log_channel_id";

    private final String generalPermission = "commands.log";

    private final List<PermissionObject> permissionObjects;
    private final String description;

    private final String help;

    public LogCommand() {
        String prefix = Bot.INSTANCE.getCommandPrefix();
        permissionObjects = new ArrayList<>();

        permissionObjects.add(new PermissionObject(generalPermission, "Execute command"));

        description = "With this command you can set the log channel for the Bots log entries!";

        help = "Log Command\n"
                + prefix + " log get/set <#channel>\n";
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

        debug("check args.length > 0");
        if (args.length == 0) {
            sendHelp(sender);
            return false;
        }

        debug("Switch args[0]: " + args[0].toLowerCase());

        Guild guild = message.getGuild();
        GuildObject guildObject = new GuildObject(guild);

        switch (args[0].toLowerCase()) {
            case "get": {
                List<String> idList = Helper.getObjectFromDB(LOG_CHANNEL_ID, TABLE, GUILD_ID + "=" + guild.getId());
                if (idList.isEmpty()) {
                    Helper.sendPrivateMessage(sender, "There is no log channel configured");
                    Helper.sendOwner("There is no log channel configured", guild);
                    return false;
                }
                String channelId = idList.get(0);

                TextChannel tx = guild.getTextChannelById(channelId);
                if (tx == null) {
                    Helper.sendPrivateMessage(sender, "Something went wrong by loading the log channel");
                    guildObject.warn("Something went wrong by loading the log channel: " + channelId);
                    return false;
                }

                Helper.sendPrivateMessage(sender, "The current log channel is: " + tx.getAsMention());
                return true;
            }
            case "set": {
                List<TextChannel> mentionedChannels = message.getMentionedChannels();
                if (mentionedChannels.isEmpty()) {
                    Helper.sendPrivateMessage(sender, "You must mention a channel!");
                    return false;
                }
                TextChannel textChannel = mentionedChannels.get(0);
                if (!Helper.executeInsertUpdateOnDuplicateSQL(
                        TABLE,
                        GUILD_ID + "," + LOG_CHANNEL_ID,
                        guild.getId() + "," + textChannel.getId(),
                        LOG_CHANNEL_ID + "=" + textChannel.getId())) {
                    guildObject.warn("Failed to insert or update the log channel: " + textChannel.getId());
                    return false;
                }

                guildObject.loadModLogChannel();
                guildObject.modLog(sender.getAsMention() + " set new mod-log channel: " + textChannel.getAsMention());
                Helper.sendPrivateMessage(sender, "You successfully change the log channel to: " +textChannel.getAsMention());
                return true;
            }
            default: {
                sendHelp(sender);
            }
        }


        return false;
    }
}
