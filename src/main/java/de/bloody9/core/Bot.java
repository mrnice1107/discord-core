package de.bloody9.core;

import de.bloody9.core.commands.CommandManager;
import de.bloody9.core.commands.bot.*;
import de.bloody9.core.commands.console.*;
import de.bloody9.core.feature.Feature;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.listener.CommandListener;
import de.bloody9.core.listener.JoinListener;
import de.bloody9.core.logging.LogLevel;
import de.bloody9.core.models.interfaces.*;
import de.bloody9.core.models.objects.BotInitObject;
import de.bloody9.core.models.objects.PermissionObject;
import de.bloody9.core.mysql.MySQLConnection;
import de.bloody9.core.permissions.*;
import de.bloody9.core.threads.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
     * @param args {@link String}[] parameters:
     *             <p>-d | name of database</p>
     *             <p>-u | mysql username</p>
     *             <p>-p | mysql password</p>
     *             <p>-t | discord bot token</p>
     *             <p>-prefix | bot command prefix</p>
     *             <p>-l | {@link LogLevel}</p>
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

    public final List<Feature> features;

    private boolean running;
    private boolean mergeModLog;
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

        initializeSQL(initObject);

        debug("setting instance");
        INSTANCE = this;

        debug("setting command prefix: " + initObject.getCommandPrefix());
        commandPrefix = initObject.getCommandPrefix();

        debug("initializing guild permission configs and permission list");
        guildPermissions = new ArrayList<>();
        permissions = new ArrayList<>();

        debug("setting some default settings");
        mergeModLog = true;

        debug("initializing features");
        features = new ArrayList<>();
        addFeatures();
        checkFeatures();

        debug("initializing commands");
        HashMap<String, BotCommand> commands = new HashMap<>();
        addBotCommands(commands);

        debug("initializing command manager");
        commandManager = new CommandManager(commands);

        debug("initializing JDA");
        if (!initializeJDA(initObject)) {
            error("Shutting down bot because bot failed to initialize JDA");
            return;
        }

        debug("loading features");
        features.stream().filter(Feature::isEnabled).forEach(Feature::load);

        afterInit(initObject);

        startUpdater();

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

    // main initialization of JDA
    private boolean initializeJDA(BotInitObject initObject) {
        try {
            debug("try get jda builder");
            JDABuilder builder = JDABuilder.createDefault(initObject.getDiscordToken());

            List<ListenerAdapter> listener = new ArrayList<>();
            addEventListener(listener);

            builder.addEventListeners(listener.toArray());

            addPermissions(permissions);

            init(initObject, builder);

            debug("build jda");
            jda = builder.build();
        } catch (LoginException | IllegalArgumentException e) {
            error(e);
            return false;
        }

        debug("done building jda");
        return true;
    }




    /*
    *
    * Initializing
    *
    * */




    /**
     * <p>This method is called before the initialization of the bot</p>
     * <p>{@link JDABuilder} is not build </p>
     */
    public void preInit(BotInitObject initObject) {
        debug("pre initializing with: " + initObject.toString());
    }

    /**
     * <p>This method is called while the initialization of the bot</p>
     * <p>This method contains the set of the initial JDA status and settings</p>
     * <p>{@link JDABuilder} is building </p>
     */
    public void init(BotInitObject initObject, JDABuilder builder) {
        debug("initializing with: " + initObject.toString());
        setInitialJDAStatus(builder);
        setInitialJDASettings(builder);
    }

    /**
     * <p>This method is called while the initialization of the bot</p>
     * <p>{@link JDABuilder} is build </p>
     */
    public void afterInit(BotInitObject initObject) {
        debug("after initializing with: " + initObject.toString());
    }




    /**
     * <p>Initialization of {@link MySQLConnection}</p>
     * Automatically called in {@link Bot} constructor
     * */
    public void initializeSQL(BotInitObject initObject) {
        debug("initialize sql");
        MySQLConnection.init(initObject);
    }

    public void initUpdater() {
        debug("adding config updater");
        List<ConfigUpdater> configUpdater = new ArrayList<>();
        addConfigUpdater(configUpdater);

        debug("initialize updater");
        updater = new Updater(configUpdater);
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




    /*
     *
     * Add
     *
     * */




    public void addFeatures() {
        debug("adding features");
    }

    public void addEventListener(List<ListenerAdapter> listener) {
        debug("add event listeners");
        listener.add(new CommandListener());
        listener.add(new JoinListener());

        debug("event listener from features");
        features.stream().filter(Feature::isEnabled).forEach(feature -> listener.addAll((feature.getListeners() != null) ? feature.getListeners() : new ArrayList<>()));
    }

    public void addBotCommands(HashMap<String, BotCommand> commands) {
        debug("adding bot commands");

        // commands must be lower case
        commands.put("help", new HelpCommand());
        commands.put("permission", new PermissionCommand());
        commands.put("log", new LogCommand());
        commands.put("activity", new ActivityCommand());
        commands.put("prefix", new PrefixCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
        commands.put("clear", new ClearCommand());

        debug("adding bot commands from features");
        features.stream().filter(Feature::isEnabled).forEach(feature -> commands.putAll(feature.getCommands()));
    }

    public void addConfigUpdater(List<ConfigUpdater> updater) {
        debug("adding config updater");
        updater.add(new GuildPermissionUpdater());

        debug("adding config updater from features");
        features.stream().filter(Feature::isEnabled).forEach(feature -> updater.addAll(feature.getConfigUpdaters()));
    }

    public void addPermissions(List<PermissionObject> permissions) {
        debug("adding permissions");
    }

    public void addConsoleCommands(HashMap<String, SimpleCommand> consoleCommands) {
        consoleCommands.put("loglevel", new CMDLogLevel());
        consoleCommands.put("update", new CMDUpdate());
    }

    public void addGuildPermission(GuildPermission guildPermission) {
        debug("adding new guildPermission object");
        this.guildPermissions.add(guildPermission);
    }




    /*
     *
     * Start / Stop
     *
     * */



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

    /**
     * <p>This method is called when the bot is shutting down</p>
     * <p>You can unload stuff here</p>
     */
    public void onShutdown() {
        debug("shutting down bot");
    }






    private void checkFeatures() {
        debug("checking features");

        List<String> featureStr = new ArrayList<>();
        features.forEach(feature -> featureStr.add(feature.getName().toLowerCase()));

        // checks if dependencies are available
        debug("check if required features are included");
        features.stream().filter(feature -> checkFeature(featureStr, feature)).forEach(Feature::enable);

        // checks if dependencies are enabled
        features.stream().filter(Feature::isEnabled).forEach(feature -> {
            if (feature.requiredFeatures().stream().allMatch(s -> getFeature(s).isEnabled())) {
                info("Feature: " + feature.toString() + " is enabled");
            } else {
                feature.disable();
                warn("Failed to load feature " + feature.toString() + " because required features are not available!");
            }
        });
    }

    private boolean checkFeature(List<String> features, Feature feature) {
        return feature.requiredFeatures().stream().map(String::toLowerCase).allMatch(features::contains);
    }






    /*
     *
     * Getter / Setter
     *
     * */


    public Feature getFeature(String name) {
        return features.stream().filter(feature -> feature.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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

    public JDA getJda() { return jda; }

    public boolean isRunning() { return running; }

    public void setRunning(boolean running) { this.running = running; }

    public CommandManager getCommandManager() { return commandManager; }

    public void setCommandPrefix(String commandPrefix) {
        info("new prefix was set: " + commandPrefix);
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() { return commandPrefix; }

    public Updater getUpdater() { return updater; }

    public void setUpdater(Updater updater) { this.updater = updater; }

    public boolean isMergeModLog() { return mergeModLog; }

    public void setMergeModLog(boolean mergeModLog) { this.mergeModLog = mergeModLog; }
}