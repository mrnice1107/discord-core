package de.bloody9.core.models.objects;

import de.bloody9.core.logging.Logger;

import net.dv8tion.jda.api.entities.Guild;

public class GuildObject {

    private final Guild guild;
    private final String guildId;
    private final String guildName;

    public GuildObject(Guild guild) {
        this.guild = guild;
        this.guildId = guild.getId();
        this.guildName = guild.getName();
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

    public String getGuildId() { return guildId; }

    public String getGuildName() { return guildName; }

    public Guild getGuild() { return guild; }
}
