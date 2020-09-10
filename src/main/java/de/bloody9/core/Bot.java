package de.bloody9.core;

import de.bloody9.core.commands.*;
import de.bloody9.core.commands.bot.HelpCommand;
import de.bloody9.core.commands.bot.PermissionCommand;
import de.bloody9.core.commands.console.CMDLogLevel;
import de.bloody9.core.commands.console.CMDUpdate;
import de.bloody9.core.config.GuildPermission;
import de.bloody9.core.config.GuildPermissionUpdater;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.interfaces.ConfigUpdater;
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

    public static BotInitObject enterArgs(String[] args) {
        String sqlUser = null, sqlPw = null, dcToken = null, prefix = null;

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i].toLowerCase();

            if (arg.startsWith("-")) {
                String next = args[i + 1];
                switch (arg) {
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
                        if (LogLevel.contains(next)) {
                            setLogLevel(LogLevel.valueOf(next));
                        } else {
                            warn("Loglevel: " + next + " is no valid loglevel");
                        }
                        break;
                    }
                    default: {
                        warn("the argument: " + arg + " dose not exists and will be ignored");
                    }
                }
            }
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

        BotInitObject initObject = new BotInitObject(sqlUser, sqlPw, dcToken, prefix);

        debug(initObject.toString());

        return initObject;
    }

    public static Bot INSTANCE;

    private final List<GuildPermission> guildPermissions;

    private boolean running;
    private JDA jda;
    private final CommandManager commandManager;
    private final String commandPrefix;

    private Updater updater;

    public Bot(String[] args) {
        this(enterArgs(args));
    }


    public Bot(BotInitObject initObject) {
        info("------------");
        info("Initializing");
        info("------------");

        preInit(initObject);

        debug("initializing guild permission configs");
        guildPermissions = new ArrayList<>();

        debug("setting instance");
        INSTANCE = this;

        debug("setting command prefix: " + initObject.getCommandPrefix());
        commandPrefix = initObject.getCommandPrefix();

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
    }

    public void afterInit(BotInitObject initObject) {
        debug("after initializing");
        
        startUpdater();
    }

    public void addConfigUpdater(List<ConfigUpdater> updater) {
        debug("adding config updater");
        updater.add(new GuildPermissionUpdater());
    }

    public void addBotCommands(HashMap<String, BotCommand> commands) {
        debug("adding bot commands");

        // commands must be lower case
        commands.put("help", new HelpCommand());
        commands.put("permission", new PermissionCommand());
    }

    public void initializeSQL(BotInitObject initObject) {
        debug("initialize sql");
        MySQLConnection.init(initObject.getSqlUser(), initObject.getSqlPassword());
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
        builder.addEventListeners(new CommandListener(prefix));
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

    public void setStatus(OnlineStatus status) {
        debug("Set activity count to: " + status);
        getJda().getPresence().setStatus(status);
    }

    public void setActivity(Activity activity) {
        debug("Set activity count to: " + activity);
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

    public String getCommandPrefix() { return commandPrefix; }

    public Updater getUpdater() { return updater; }

    public void setUpdater(Updater updater) { this.updater = updater; }
}