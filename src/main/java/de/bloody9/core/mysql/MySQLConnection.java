package de.bloody9.core.mysql;

import static de.bloody9.core.helper.Helper.censor;
import de.bloody9.core.models.objects.BotInitObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.bloody9.core.logging.Logger.*;

public class MySQLConnection {
    private static String user;
    private static String password;
    private static String url;

    public static void init(BotInitObject botInitObject) {
        init(botInitObject.getSqlDatabase(), botInitObject.getSqlUser(), botInitObject.getSqlPassword());
    }
    public static void init(String database, String user, String password) {
        info("initializing SQL connector");
        debug("database: " + database);
        debug("user: " + censor(user));
        debug("password: " + censor(password));
        MySQLConnection.user = user;
        MySQLConnection.password = password;
        MySQLConnection.url = getUrl(database);
        try {
            debug("Checking DB connection");
            getConnection();
            debug("Successfully connected");
        } catch (SQLException ex) {
            error("Failed to connect to DB");
            error(ex);
        }
    }

    private static String getUrl(String databaseName) {
        return "jdbc:mysql://localhost:3306/" + ((databaseName == null || databaseName.equals("")) ? "discord" : databaseName) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean executeUpdate(Set<String> commands) {
        return executeUpdate(new ArrayList<>(commands));
    }

    public static boolean executeUpdate(List<String> commands) {
        debug("executing sql updates: " + commands.toString());
        try {
            Connection con = getConnection();
            Statement stat = con.createStatement();
            for (String command : commands) {
                debug("executing sql command: " + command);
                stat.executeUpdate(command);
            }

            stat.close();
            con.close();
            debug("successfully executed sql commands");
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    public static boolean executeUpdate(String command) {
        debug("executing sql update: " + command);
        try {
            Connection con = getConnection();
            Statement stat = con.createStatement();
            stat.executeUpdate(command);

            stat.close();
            con.close();
            debug("successfully executed sql command");
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        MySQLConnection.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MySQLConnection.password = password;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String databaseName) {
        MySQLConnection.url = getUrl(databaseName);
    }
}
