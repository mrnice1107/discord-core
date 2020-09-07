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

    public void setSqlUser(String sqlUser) {
        SqlUser = sqlUser;
    }

    public String getSqlPassword() {
        return SqlPassword;
    }

    public void setSqlPassword(String sqlPassword) {
        SqlPassword = sqlPassword;
    }

    public String getDiscordToken() {
        return DiscordToken;
    }

    public void setDiscordToken(String discordToken) {
        DiscordToken = discordToken;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }
}
