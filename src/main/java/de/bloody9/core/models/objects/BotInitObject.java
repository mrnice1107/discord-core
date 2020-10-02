package de.bloody9.core.models.objects;

/**
 * This object includes required information for the discord bot to start
 * includes:
 * sql database name
 * sql user
 * sql password
 * discord bot token
 * bot command prefix
 */
public class BotInitObject {
    private String SqlDatabase;
    private String SqlUser;
    private String SqlPassword;

    private String DiscordToken;

    private String commandPrefix;

    public BotInitObject(String SqlDatabase, String sqlUser, String sqlPassword, String discordToken, String commandPrefix) {
        this.SqlDatabase = SqlDatabase;
        this.SqlUser = sqlUser;
        this.SqlPassword = sqlPassword;
        this.DiscordToken = discordToken;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public String toString() {
        return "BotInitObject{" +
                "SqlUser='" + SqlUser + '\'' +
                ", SqlPassword='" + SqlPassword + '\'' +
                ", DiscordToken='" + DiscordToken + '\'' +
                ", commandPrefix='" + commandPrefix + '\'' +
                '}';
    }

    public String getSqlDatabase() {
        return SqlDatabase;
    }

    public BotInitObject setSqlDatabase(String sqlDatabase) {
        SqlDatabase = sqlDatabase;
        return this;
    }

    public String getSqlUser() {
        return SqlUser;
    }

    public BotInitObject setSqlUser(String sqlUser) {
        SqlUser = sqlUser;
        return this;
    }

    public String getSqlPassword() {
        return SqlPassword;
    }

    public BotInitObject setSqlPassword(String sqlPassword) {
        SqlPassword = sqlPassword;
        return this;
    }

    public String getDiscordToken() {
        return DiscordToken;
    }

    public BotInitObject setDiscordToken(String discordToken) {
        DiscordToken = discordToken;
        return this;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public BotInitObject setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        return this;
    }
}
