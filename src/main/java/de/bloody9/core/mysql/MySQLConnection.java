package de.bloody9.core.mysql;


// logging

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.bloody9.core.logging.Logger.*;

public class MySQLConnection {
    private static String user;
    private static String password;
    private static final String url = "jdbc:mysql://localhost:3306/discord?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static void init(String user, String password) {
        info("initializing SQL connector");
        debug("user: " + user);
        debug("password: " + password);
        MySQLConnection.user = user;
        MySQLConnection.password = password;
        try {
            debug("Checking DB connection");
            getConnection();
            debug("Successfully connected");
        } catch (SQLException ex) {
            error("Failed to connect to DB");
            error(ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean executeUpdate(List<String> commands) {
        return executeUpdate(new HashSet<>(commands));
    }

    public static boolean executeUpdate(Set<String> commands) {
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
}
