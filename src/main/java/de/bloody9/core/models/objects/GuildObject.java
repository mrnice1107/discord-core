package de.bloody9.core.models.objects;

//logging
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


    public void debug(String message) {
        Logger.debug(getGuildPrefix() + message, 1);
    }

    public void info(String message) {
        Logger.info(getGuildPrefix() + message, 1);
    }

    public void warn(String message) {
        Logger.warn(getGuildPrefix() + message, 1);
    }

    public void error(String message) {
        Logger.error(getGuildPrefix() + message, 1);
    }

    public void log(String message) {
        Logger.log(getGuildPrefix() + message, 1);
    }


    public String getGuildPrefix() {
        return getGuildPrefix(guild);
    }

    public static String getGuildPrefix(Guild guild) {
        return guild.getId() + ":" + guild.getName() + " -> ";
    }

    /*
    *
    *
    * Getter / Setter
    *
    *
    * */

    public String getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public Guild getGuild() {
        return guild;
    }
}
