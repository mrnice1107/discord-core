package de.bloody9.core.helper;

import de.bloody9.core.config.GuildPermission;
import de.bloody9.core.mysql.MySQLConnection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static List<String> getObjectFromDB(@NotNull String column, @NotNull String table, @Nullable String query) {
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

        List<String> results = new ArrayList<>();
        try {
            Connection con = MySQLConnection.getConnection();
            debug("building connection: " + con.toString());
            Statement stat = con.createStatement();
            debug("statement: " + stat.toString());
            ResultSet resultSet = stat.executeQuery(builder.toString());
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


    public static boolean hasPermission(@NotNull String permission, Member member) {
        debug("has permission: " + permission);
        if (member == null) {
            error("Can't check permission because member is null: " + permission);
            return false;
        }
        debug("member: " + member.getUser().getName());

        Guild guild = member.getGuild();

        GuildPermission guildPermission = GuildPermission.getGuildPermissionByID(guild.getId());
        guildPermission.debug("guildPermission: " + guildPermission.toString());

        boolean hasPerm = guildPermission.hasPermission(permission, member);

        if (!hasPerm) {
            Member owner = member.getGuild().getOwner();
            if (owner != null && owner.getId().equals(member.getId())) {
                guildPermission.debug("member is guild owner");
                Helper.sendPrivateMessage(member, "You don't have the permission *" + permission + "* but you are owner so you can do this anyway :D");
                return true;
            } else {
                guildPermission.debug("member has no permission");
                sendPrivateMessage(member, "You have not enough permission to do this!\nMissing permission:*" + permission + "*");
            }
        }
        guildPermission.debug("member has permission");

        return hasPerm;
    }


    public static String getTime() {
        Date date = new Date();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }



    public static boolean executeInsertSQL(String table, String contentType, String content) {
        return MySQLConnection.executeUpdate(getInsertSQL(table, contentType, content));
    }

    public static String getInsertSQL(String table, String contentType, String content) {
        String sql = "INSERT INTO <table> (<contentType>) VALUES(<content>);";
        sql = sql.replace("<table>", table).replace("<contentType>", contentType).replace("<content>", content);

        return sql;
    }


    public static boolean executeDeleteSQL(String table, String query) {
        return MySQLConnection.executeUpdate(getDeleteSQL(table, query));
    }

    public static String getDeleteSQL(String table, String query) {
        String sql = "DELETE FROM <table> WHERE <query>;";
        sql = sql.replace("<table>", table).replace("<query>", query);

        return sql;
    }

    public static String getLineFromConsole(String out) {
        System.out.println(out);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }
}