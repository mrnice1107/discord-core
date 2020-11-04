package de.bloody9.core.models.objects;

import de.bloody9.core.Bot;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.logging.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.nio.charset.Charset;
import java.util.List;

public class GuildObject {
    private static final String TABLE = "guild_logs";
    private static final String GUILD_ID = "guild_id";
    private static final String LOG_CHANNEL_ID = "log_channel_id";

    private final Guild guild;
    private final String guildId;
    private final String guildName;

    private TextChannel modLogChannel = null;

    public GuildObject(Guild guild) {
        this.guild = guild;
        this.guildId = guild.getId();
        this.guildName = guild.getName();


    }

    public void loadModLogChannel() {
        List<String> idList = Helper.getObjectFromDB(LOG_CHANNEL_ID, TABLE, GUILD_ID + "=" + guild.getId());
        if (idList.isEmpty()) {
            Helper.sendOwner("There is no log channel configured", guild);
            return;
        }
        String channelId = idList.get(0);

        TextChannel tx = guild.getTextChannelById(channelId);
        if (tx == null) {
            warn("Something went wrong by loading the log channel: " + channelId);
            return;
        }
        modLogChannel = tx;
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
            if (!modlogMerge(message)) modLogChannel.sendMessage(message).queue();
        }
        return this;
    }

    private boolean modlogMerge(CharSequence message) {
        Bot INSTANCE = Bot.INSTANCE;
        if (INSTANCE.isMergeModLog()) {
            String lastMessage = modLogChannel.getLatestMessageId();
            Message msg = modLogChannel.retrieveMessageById(lastMessage).complete();
            if (msg.getAuthor().getId().equals(INSTANCE.getJda().getSelfUser().getId())
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

    public TextChannel getModLogChannel() {
        return modLogChannel;
    }

    public String getGuildId() { return guildId; }

    public String getGuildName() { return guildName; }

    public Guild getGuild() { return guild; }
}
