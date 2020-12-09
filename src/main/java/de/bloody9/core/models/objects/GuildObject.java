package de.bloody9.core.models.objects;

import de.bloody9.core.Bot;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.logging.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuildObject {

    public enum ModLogStatus {
        LOADED,
        DELETED_CHANNEL,
        NOT_CONFIGURED
    }

    private static final String TABLE = "guild_logs";
    private static final String GUILD_ID = "guild_id";
    private static final String LOG_CHANNEL_ID = "log_channel_id";

    private final Guild guild;
    private final String guildId;
    private final String guildName;

    private TextChannel modLogChannel = null;

    private ModLogStatus modLogStatus = null;

    public GuildObject(Guild guild) {
        this.guild = guild;
        this.guildId = guild.getId();
        this.guildName = guild.getName();
    }

    public void loadModLogChannel() {
        List<String> idList = Helper.getObjectFromDB(LOG_CHANNEL_ID, TABLE, GUILD_ID + "=" + guild.getId());
        if (idList.isEmpty()) {
            Helper.sendOwner("There is no log channel configured", guild);
            modLogStatus = ModLogStatus.NOT_CONFIGURED;
            return;
        }
        String channelId = idList.get(0);

        TextChannel tx = guild.getTextChannelById(channelId);
        if (tx == null) {
            warn("Something went wrong by loading the log channel: " + channelId);
            modLogStatus = ModLogStatus.DELETED_CHANNEL;
            return;
        }
        modLogChannel = tx;
        modLogStatus = ModLogStatus.LOADED;
    }

    public GuildObject modLog(CharSequence message, EmbedBuilder builder) {
        info(message);

        if (modLogChannel == null) {
            loadModLogChannel();
        }
        if (modLogChannel != null) {
            modLogChannel.sendMessage(builder.build()).queue();
        }
        builder.clear();
        return this;
    }
    public GuildObject modLog(Object obj) { modLog(String.valueOf(obj)); return this; }
    public GuildObject modLog(CharSequence message) {
        info(message);

        if (modLogChannel == null) {
            loadModLogChannel();
        }
        if (modLogChannel != null) {
            if (!modLogMerge(message)) modLogChannel.sendMessage(message).queue();
        }
        return this;
    }

    private boolean modLogMerge(CharSequence message) {
        Bot INSTANCE = Bot.INSTANCE;
        if (INSTANCE.isMergeModLog()) {
            String lastMessage;
            Message msg;
            try {
                lastMessage = modLogChannel.getLatestMessageId();
                msg = modLogChannel.retrieveMessageById(lastMessage).complete();
            } catch (IllegalStateException | ErrorResponseException e) {
                warn("last log message got deleted!");
                return false;
            }
            if (msg != null
                    && msg.getAuthor().getId().equals(INSTANCE.getJda().getSelfUser().getId())
                    && !Helper.checkMessageOlderThen(msg, 10)
                    && msg.getEmbeds().isEmpty()) {
                msg.editMessage(msg.getContentRaw() + "\n" + message).queue();
                return true;
            }
        }
        return false;
    }

    public String modLogMessage(Object obj) { return modLogMessage(String.valueOf(obj)); }
    public String modLogMessage(CharSequence message) {
        info(message);

        if (modLogChannel == null) {
            loadModLogChannel();
        }

        if (modLogChannel != null) {
            return modLogChannel.sendMessage(message).complete().getId();
        }
        return null;
    }

    public GuildObject test(CharSequence message) { Logger.test(getGuildPrefix() + message, 1); return this; }
    public GuildObject test(Object obj) { test(String.valueOf(obj)); return this; }

    public GuildObject debug(CharSequence message) { Logger.debug(getGuildPrefix() + message, 1); return this; }
    public GuildObject debug(Object obj) { debug(String.valueOf(obj)); return this; }

    public GuildObject info(CharSequence message) { Logger.info(getGuildPrefix() + message, 1); return this; }
    public GuildObject info(Object obj) { info(String.valueOf(obj)); return this; }

    public GuildObject warn(CharSequence message) { Logger.warn(getGuildPrefix() + message, 1); return this; }
    public GuildObject warn(Object obj) { warn(String.valueOf(obj)); return this; }

    public GuildObject error(CharSequence message) { Logger.error(getGuildPrefix() + message, 1); return this; }
    public GuildObject error(Object obj) { error(String.valueOf(obj)); return this; }

    public GuildObject log(CharSequence message) { Logger.log(getGuildPrefix() + message, 1); return this; }
    public GuildObject log(Object obj) { log(String.valueOf(obj)); return this; }


    public String getGuildPrefix() { return getGuildPrefix(guild); }

    public static String getGuildPrefix(Guild guild) { return guild.getId() + ":" + guild.getName() + " -> "; }

    /*
    *
    *
    * Getter / Setter
    *
    *
    * */

    public boolean setModLogChannel(@NotNull TextChannel channel, @NotNull String modifier) {
        if (setModLogChannel(channel)) {
            modLog(modifier + " set new mod-log channel: " + channel.getAsMention());
            return true;
        }
        return false;
    }

    private boolean setModLogChannel(@NotNull TextChannel channel) {

        boolean result;
        if (result = Helper.executeInsertUpdateOnDuplicateSQL(
                TABLE,
                GUILD_ID + "," + LOG_CHANNEL_ID,
                getGuildId() + "," + channel.getId(),
                LOG_CHANNEL_ID + "=" + channel.getId())) {
            modLogChannel = channel;
        }
        else { warn("Failed to insert or update the log channel: " + channel.getId()); }

        return result;
    }

    public TextChannel getModLogChannel() {
        if (modLogChannel == null) { loadModLogChannel(); }
        return modLogChannel;
    }

    public ModLogStatus getModLogStatus() {
        if (modLogStatus == null) { loadModLogChannel(); }
        return modLogStatus;
    }

    public String getGuildId() { return guildId; }

    public String getGuildName() { return guildName; }

    public Guild getGuild() { return guild; }
}
