package de.bloody9.core.feature;

import de.bloody9.core.Bot;
import de.bloody9.core.logging.Logger;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.interfaces.ConfigUpdater;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Feature {

    private final Map<String, BotCommand> commands;
    private final List<ListenerAdapter> listeners;
    private final List<ConfigUpdater> configUpdaters;

    private boolean enabled;

    /**
     * <p>Add all for the feature required
     * {@link BotCommand},
     * {@link ListenerAdapter},
     * {@link ConfigUpdater}
     * to the constructor.</p>
     */
    public Feature() {
        commands = new HashMap<>();
        listeners = new ArrayList<>();
        configUpdaters = new ArrayList<>();
    }

    /**
     * @return returns the name of the features
     * */
    @NotNull
    public String getName() {
        return "EmptyFeature";
    }

    /**
     * @return returns the version of the features
     * */
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    /**
     * @return returns a list of features that are required for ths feature to work
     * */
    @NotNull
    public List<String> requiredFeatures() {
        return new ArrayList<>();
    }

    public Bot getBot() {
        return Bot.INSTANCE;
    }

    @Override
    public String toString() {
        return getName() + ":" + getVersion();
    }

    /**
     * called when loading the feature
     */
    public void load() {
        if (!isEnabled()) return;
        debug("load feature" + toString());
    }

    /**
     * called when unloading the feature
     */
    public void unload() {
        debug("unload feature" + toString());
    }





    //
    //
    // Adding classes
    //
    //

    //    private final Map<String, BotCommand> commands;
    //    private final List<ListenerAdapter> listeners;
    //    private final List<ConfigUpdater> configUpdaters;

    /**
     * Here you can add the features commands
     */
    public Feature addCommand(String key, BotCommand command) {
        commands.put(key, command);

        return this;
    }

    /**
     * Here you can add the features listeners
     */
    public Feature addListener(ListenerAdapter listener) {
        listeners.add(listener);

        return this;
    }

    /**
     * Here you can add the features configUpdaters
     */
    public Feature addUpdater(ConfigUpdater configUpdater) {
        configUpdaters.add(configUpdater);

        return this;
    }







    //
    //
    // Logging
    //
    //





    public Feature debug(CharSequence message) { Logger.debug(getPrefix() + message, 1); return this; }
    public Feature debug(Object obj) { return debug(String.valueOf(obj)); }

    public Feature info(CharSequence message) { Logger.info(getPrefix() + message, 1); return this; }
    public Feature info(Object obj) { return info(String.valueOf(obj)); }

    public Feature warn(CharSequence message) { Logger.warn(getPrefix() + message, 1); return this; }
    public Feature warn(Object obj) { return warn(String.valueOf(obj)); }

    public Feature error(CharSequence message) { Logger.error(getPrefix() + message, 1); return this; }
    public Feature error(Object obj) { return error(String.valueOf(obj)); }

    public Feature log(CharSequence message) { Logger.log(getPrefix() + message, 1); return this; }
    public Feature log(Object obj) { return log(String.valueOf(obj)); }

    public String getPrefix() {
        return "[" + getName().toLowerCase() + "] ";
    }






    //
    //
    // Getter / Setter
    //
    //








    /**
     * Disable the feature
     * */
    public Feature disable() {
        enabled = false;
        return this;
    }
    /**
     * Enable the feature
     * */
    public Feature enable() {
        enabled = true;
        return this;
    }

    /**
     * Disable or enable the feature
     * */
    public Feature setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * @return returns if the feature is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }

    public List<ListenerAdapter> getListeners() {
        return listeners;
    }

    public List<ConfigUpdater> getConfigUpdaters() {
        return configUpdaters;
    }
}