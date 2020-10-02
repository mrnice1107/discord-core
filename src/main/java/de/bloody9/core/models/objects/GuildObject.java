package de.bloody9.core.models.objects;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.logging.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public void modLog(Object obj) { modLog(String.valueOf(obj)); }
    public void modLog(String message) {
        info(message);

        if (modLogChannel == null) {
            loadModLogChannel();
        }
        if (modLogChannel != null) {
            modLogChannel.sendMessage(message).queue();
        }
    }

    public String modLogMessage(Object obj) { return modLogMessage(String.valueOf(obj)); }
    public String modLogMessage(String message) {
        info(message);

        if (modLogChannel == null) {
            loadModLogChannel();
        }

        if (modLogChannel != null) {
            String msgId;
            return modLogChannel.sendMessage(message).complete().getId();
        }
        return null;
    }

    public void debug(CharSequence message) { Logger.debug(getGuildPrefix() + message, 1); }
    public void debug(Object obj) { debug(String.valueOf(obj)); }

    public void info(CharSequence message) { Logger.info(getGuildPrefix() + message, 1); }
    public void info(Object obj) { info(String.valueOf(obj)); }

    public void warn(CharSequence message) { Logger.warn(getGuildPrefix() + message, 1); }
    public void warn(Object obj) { warn(String.valueOf(obj)); }

    public void error(CharSequence message) { Logger.error(getGuildPrefix() + message, 1); }
    public void error(Object obj) { error(String.valueOf(obj)); }

    public void log(CharSequence message) { Logger.log(getGuildPrefix() + message, 1); }
    public void log(Object obj) { log(String.valueOf(obj)); }


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
