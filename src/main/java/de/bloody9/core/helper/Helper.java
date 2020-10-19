package de.bloody9.core.helper;

import de.bloody9.core.Bot;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.permissions.GuildPermission;
import de.bloody9.core.mysql.MySQLConnection;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static de.bloody9.core.logging.Logger.debug;
import static de.bloody9.core.logging.Logger.error;
import static de.bloody9.core.models.objects.GuildObject.getGuildPrefix;


public class Helper {
    public static void sendPrivateMessage(@NotNull User user, String message) {
        debug("sending private message: " + message + " to user: " + user.getName() + ":" + user.getId());
        user.openPrivateChannel().complete().sendMessage(message).queue();
    }
    public static void sendPrivateMessage(@NotNull Member member, String message) {
        sendPrivateMessage(member.getUser(), message);
    }

    public static void sendOwner(String message, @NotNull Guild guild) {
        Member owner = guild.getOwner();
        if (owner != null) {
            debug(getGuildPrefix(guild) + "send message to guild owner: " + owner.getUser().getName());
            sendPrivateMessage(owner.getUser(), message);
        }
    }



    public static boolean isOwner(@NotNull Member member) {
        return isOwner(member.getUser(), member.getGuild());
    }
    public static boolean isOwner(@NotNull User user, @NotNull Guild guild) {
        return user.getId().equals(guild.getOwnerId());
    }

    public static List<String> getObjectFromDB(@NotNull String column, @NotNull String table, @Nullable String query) {
        final String sqlQuery = constructQueryString(column, table, query);

        final List<String> results = new ArrayList<>();
        try {
            Connection con = MySQLConnection.getConnection();
            debug("building connection: " + con.toString());
            Statement stat = con.createStatement();
            debug("statement: " + stat.toString());
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            debug("query result: " + resultSet.toString());

            while (resultSet.next()) {
                results.add(resultSet.getString(column));
            }

            resultSet.close();
            stat.close();
            con.close();
        } catch (SQLException e) {
            error(e);
        }

        return results;
    }
    public static List<Integer> getIntegerFromDB(@NotNull String column, @NotNull String table, @Nullable String query) {
        String sqlQuery = constructQueryString(column, table, query);

        List<Integer> results = new ArrayList<>();
        try {
            Connection con = MySQLConnection.getConnection();
            debug("building connection: " + con.toString());
            Statement stat = con.createStatement();
            debug("statement: " + stat.toString());
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            debug("query result: " + resultSet.toString());

            while (resultSet.next()) {
                results.add(resultSet.getInt(column));
            }

            resultSet.close();
            stat.close();
            con.close();
        } catch (SQLException e) {
            error(e);
        }

        return results;
    }
    public static List<Boolean> getBooleanFromDB(@NotNull String column, @NotNull String table, @Nullable String query) {
        String sqlQuery = constructQueryString(column, table, query);

        List<Boolean> results = new ArrayList<>();
        try {
            Connection con = MySQLConnection.getConnection();
            debug("building connection: " + con.toString());
            Statement stat = con.createStatement();
            debug("statement: " + stat.toString());
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            debug("query result: " + resultSet.toString());

            while (resultSet.next()) {
                results.add(resultSet.getBoolean(column));
            }

            resultSet.close();
            stat.close();
            con.close();
        } catch (SQLException e) {
            error(e);
        }

        return results;
    }

    public static String getFirstObjectFromDB(@NotNull String column, @NotNull String table, @Nullable String query) {
        List<String> list = getObjectFromDB(column, table, query);
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public static String constructQueryString(@NotNull String column, @NotNull String table, @Nullable String query) {
        debug("getting object from DB");
        //example query "SELECT <column> FROM <table> WHERE <query>"
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT ").append(column);
        builder.append(" FROM ").append(table);

        if (query != null && !query.equals(""))  {
            builder.append(" WHERE ").append(query);
        }

        builder.append(";");
        debug("query string: " + builder.toString());
        return builder.toString();
    }


    public static void createFile(String path) throws IOException {
        createFile(new File(path));
    }

    public static void createFile(File file) throws IOException {
        if (file.getParentFile().mkdirs()) {
            System.out.println("Directory created: " + file.getAbsolutePath());
        } else {
            System.out.println("Directory already exists.");
        }
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {
            System.out.println("File already exists.");
        }
    }

    public static String getTime() {
        return getTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTime(String format) {
        Date date = new Date();
        return new SimpleDateFormat(format).format(date);
    }

    public static String constructHelp(String helpMessage) {
        return helpMessage.replace("<prefix>", getCommandPrefix());
    }

    public static String getCommandPrefix() {
        return Bot.INSTANCE.getCommandPrefix();
    }

    public static String censor(String toCensor) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char character : toCensor.toCharArray()) stringBuilder.append((Math.random() * 100 < 70) ? "*": character);
        return stringBuilder.toString();
    }

    public static String getCommandHelp(String command) {
        command = command.toLowerCase();
        BotCommand cmd = Bot.INSTANCE.getCommandManager().getCommands().get(command);
        StringBuilder builder = new StringBuilder();

        builder.append("\n").append(command).append(": ");
        if (cmd != null) {
            builder
                    .append("\nDescription: ").append(cmd.getDescription())
                    .append("\nHelp: ").append(cmd.getHelp())
                    .append("\nAliases: ").append(cmd.getAlias());
        } else {
            builder.append("This command dose not exist");
        }
        return builder.toString();
    }

    public static String getInsertSQL(String table, String contentType, String content) {
        String sql = "INSERT INTO <table> (<contentType>) VALUES(<content>);";
        sql = sql.replace("<table>", table).replace("<contentType>", contentType).replace("<content>", content);

        return sql;
    }

    public static String getDeleteSQL(String table, String query) {
        String sql = "DELETE FROM <table> WHERE <query>;";
        sql = sql.replace("<table>", table).replace("<query>", query);

        return sql;
    }

    public static String getInsertUpdateOnDuplicateSQL(String table, String contentType, String content, String queryOnUpdate) {
        String sql = getInsertSQL(table, contentType, content);
        return sql.substring(0, sql.length()-1) + " ON DUPLICATE KEY UPDATE " + queryOnUpdate + ";";
    }


    public static boolean executeInsertSQL(String table, String contentType, String content) {
        return MySQLConnection.executeUpdate(getInsertSQL(table, contentType, content));
    }

    public static boolean executeDeleteSQL(String table, String query) { return MySQLConnection.executeUpdate(getDeleteSQL(table, query)); }

    public static boolean executeInsertUpdateOnDuplicateSQL(String table, String contentType, String content, String onUpdate) { return MySQLConnection.executeUpdate(getInsertUpdateOnDuplicateSQL(table, contentType, content, onUpdate)); }



    public static String getLineFromConsole(String out) {
        System.out.println(out);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public static String getActivityAsString(@NotNull Activity activity) {
        String type = activity.getType().name().toLowerCase();
        return (type.equals("default")) ? "playing" : type + " " + activity.getName();
    }









    @Deprecated
    public static boolean hasPermission(@NotNull String permission, @NotNull Member member) {
        debug("check if member has permission");
        debug("permission: " + permission);
        debug("member: " + member.getUser().getName());

        Guild guild = member.getGuild();

        GuildPermission guildPermission = GuildPermission.getGuildPermissionByID(guild.getId());
        boolean hasPerm = guildPermission.hasPermission(permission, member);

        if (!hasPerm) {
            Member owner = member.getGuild().getOwner();
            if (owner != null && owner.getId().equals(member.getId())) {
                guildPermission.debug("member is guild owner");
                sendPrivateMessage(member, "You don't have the permission *" + permission + "* but you are owner so you can do this anyway :D");
                return true;
            } else {
                guildPermission.debug("member has no permission");
                sendPrivateMessage(member, "You have not enough permission to do this!\nMissing permission:*" + permission + "*");
            }
        }
        guildPermission.debug("member has permission");

        return hasPerm;
    }
}
