package de.bloody9.core;

import de.bloody9.core.commands.CommandManager;
import de.bloody9.core.commands.bot.*;
import de.bloody9.core.commands.console.*;
import de.bloody9.core.logging.Logger;
import de.bloody9.core.permissions.*;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.interfaces.ConfigUpdater;
import de.bloody9.core.models.objects.PermissionObject;
import de.bloody9.core.mysql.MySQLConnection;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.listener.CommandListener;
import de.bloody9.core.logging.LogLevel;
import de.bloody9.core.models.objects.BotInitObject;
import de.bloody9.core.models.interfaces.SimpleCommand;
import de.bloody9.core.threads.ConsoleCommandReader;
import de.bloody9.core.threads.Updater;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.bloody9.core.logging.Logger.*;

public class Bot {


    /**
     * With this method a {@link BotInitObject} will get generated with the given parameters.
     * When the parameters are not given, it will ask to enter them via command line.
     * @param args parameters parameters:
     *             <p>-d | name of database
     *             <p>-u | mysql username<p>
     *             <p>-p | mysql password<p>
     *             <p>-t | discord bot token<p>
     *             <p>-prefix | bot command prefix<p>
     *             <p>-l | {@link LogLevel}
     * @return {@link BotInitObject}
     */
    public static BotInitObject enterArgs(String[] args) {
        String sqlUser = null, sqlPw = null, dcToken = null, prefix = null, sqlDatabase = null;

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i].toLowerCase();

            if (arg.startsWith("-")) {
                String next = args[i + 1];
                switch (arg) {
                    case "-d": {
                        sqlDatabase = next;
                        i++;
                        break;
                    }
                    case "-u": {
                        sqlUser = next;
                        i++;
                        break;
                    }
                    case "-p": {
                        sqlPw = next;
                        i++;
                        break;
                    }
                    case "-t": {
                        dcToken = next;
                        i++;
                        break;
                    }
                    case "-prefix": {
                        prefix = next;
                        i++;
                        break;
                    }
                    case "-l": {
                        i++;
                        next = next.toUpperCase();
                        if (LogLevel.contains(next)) {
                            setLogLevel(LogLevel.valueOf(next));
                        } else {
                            warn("Loglevel: \"" + next + "\" is no valid loglevel");
                        }
                        break;
                    }
                    default: {
                        warn("the argument: " + arg + " dose not exists and will be ignored");
                    }
                }
            }
        }

        if (sqlDatabase == null) {
            sqlDatabase = Helper.getLineFromConsole("Please enter the sql database:");
        }
        if (sqlUser == null) {
            sqlUser = Helper.getLineFromConsole("Please enter the sql user:");
        }
        if (sqlPw == null) {
            sqlPw = Helper.getLineFromConsole("Please enter the sql password:");
        }
        if (dcToken == null) {
            dcToken = Helper.getLineFromConsole("Please enter the discord bot token:");
        }
        if (prefix == null) {
            prefix = Helper.getLineFromConsole("Please enter the bots command prefix:");
        }

        BotInitObject initObject = new BotInitObject(sqlDatabase, sqlUser, sqlPw, dcToken, prefix);

        debug(initObject.toString());

        return initObject;
    }

    public static Bot INSTANCE;

    private final List<GuildPermission> guildPermissions;

    private final List<PermissionObject> permissions;

    private boolean running;
    private JDA jda;
    private final CommandManager commandManager;
    private String commandPrefix;

    private Updater updater;

    public Bot(String[] args) {
        this(enterArgs(args));
    }


    public Bot(BotInitObject initObject) {
        info("------------");
        info("Initializing");
        info("------------");

        preInit(initObject);

        debug("setting instance");
        INSTANCE = this;

        debug("setting command prefix: " + initObject.getCommandPrefix());
        commandPrefix = initObject.getCommandPrefix();

        debug("initializing guild permission configs");
        guildPermissions = new ArrayList<>();

        debug("initializing permission list");
        permissions = new ArrayList<>();

        debug("initializing commands");
        HashMap<String, BotCommand> commands = new HashMap<>();
        addBotCommands(commands);

        debug("initializing command manager");
        commandManager = new CommandManager(commands);

        debug("loading JDA");
        try {
            debug("try get jda builder");
            JDABuilder builder = JDABuilder.createDefault(initObject.getDiscordToken());

            init(initObject, builder);

            debug("build jda");
            jda = builder.build();
        } catch (LoginException e) {
            error(e);
        }
        debug("jda was build successfully");

        afterInit(initObject);

        running = true;

        info("-----------");
        info("Bot Running");
        info("-----------");

        startConsoleCommandReader();

        if (updater != null) {
            try {
                updater.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        debug("close logger");
        close();
    }

    public void preInit(BotInitObject initObject) {
        debug("pre initializing");
        initializeSQL(initObject);
    }

    public void init(BotInitObject initObject, JDABuilder builder) {
        debug("initializing");
        setInitialJDAStatus(builder);
        setInitialJDASettings(builder);
        addEventListener(builder, initObject.getCommandPrefix());
        addPermissions(permissions);
    }

    public void afterInit(BotInitObject initObject) {
        debug("after initializing");

        startUpdater();
    }

    public void onShutdown() {
        debug("shutting down bot");
    }

    public void addConfigUpdater(List<ConfigUpdater> updater) {
        debug("adding config updater");
        updater.add(new GuildPermissionUpdater());
    }

    public void addPermissions(List<PermissionObject> permissions) {
        debug("adding permissions");
    }

    public void addBotCommands(HashMap<String, BotCommand> commands) {
        debug("adding bot commands");

        // commands must be lower case
        commands.put("help", new HelpCommand());
        commands.put("permission", new PermissionCommand());
        commands.put("log", new LogCommand());
        commands.put("activity", new ActivityCommand());
        commands.put("prefix", new PrefixCommand());
    }

    public void initializeSQL(BotInitObject initObject) {
        debug("initialize sql");
        MySQLConnection.init(initObject);
    }

    public void setInitialJDAStatus(JDABuilder builder) {
        debug("set activity and status");
        builder.setStatus(OnlineStatus.ONLINE);
    }

    public void setInitialJDASettings(JDABuilder builder) {
        debug("set jda settings");
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
    }

    public void addEventListener(JDABuilder builder, String prefix) {
        debug("add event listeners");
        builder.addEventListeners(new CommandListener());
    }

    public void addConsoleCommands(HashMap<String, SimpleCommand> consoleCommands) {
        consoleCommands.put("loglevel", new CMDLogLevel());
        consoleCommands.put("update", new CMDUpdate());
    }

    public void startConsoleCommandReader() {
        HashMap<String, SimpleCommand> commands = new HashMap<>();
        addConsoleCommands(commands);
        new ConsoleCommandReader(commands).start();
    }

    public void startUpdater() {
        initUpdater();

        debug("starting updater");
        updater.start();
    }

    public void initUpdater() {
        debug("adding config updater");
        List<ConfigUpdater> configUpdater = new ArrayList<>();
        addConfigUpdater(configUpdater);

        debug("initialize updater");
        updater = new Updater(configUpdater);
    }

    public List<PermissionObject> getPermissions() {
        return permissions;
    }

    public void setStatus(OnlineStatus status) {
        debug("Set online to: " + status);
        getJda().getPresence().setStatus(status);
    }

    public void setActivity(Activity activity) {
        info("Set activity: " + Helper.getActivityAsString(activity));
        getJda().getPresence().setActivity(activity);
    }

    public List<GuildPermission> getGuildPermissions() {
        return guildPermissions;
    }

    public void addGuildPermission(GuildPermission guildPermission) {
        debug("adding new guildPermission object");
        this.guildPermissions.add(guildPermission);
    }

    public JDA getJda() { return jda; }

    public boolean isRunning() { return running; }

    public void setRunning(boolean running) { this.running = running; }

    public CommandManager getCommandManager() { return commandManager; }

    public void setCommandPrefix(String commandPrefix) {
        Logger.info("new prefix was set: " + commandPrefix);
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() { return commandPrefix; }

    public Updater getUpdater() { return updater; }

    public void setUpdater(Updater updater) { this.updater = updater; }
}