package de.bloody9.core.models.objects;

public class BotInitObject {
    private String SqlUser;
    private String SqlPassword;

    private String DiscordToken;

    private String commandPrefix;

    public BotInitObject(String sqlUser, String sqlPassword, String discordToken, String commandPrefix) {
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
