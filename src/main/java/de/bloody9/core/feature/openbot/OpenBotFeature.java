package de.bloody9.core.feature.openbot;

import de.bloody9.core.commands.bot.ActivityCommand;
import de.bloody9.core.commands.bot.PrefixCommand;
import de.bloody9.core.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class OpenBotFeature extends Feature {

    public static Feature INSTANCE;

    public OpenBotFeature() {
        super();

        INSTANCE = this;

        addCommands();
    }

    @Override
    public @NotNull String getName() {
        return "OpenBotFeature";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public void addCommands() {
        addCommand("prefix", new PrefixCommand());
        addCommand("activity", new ActivityCommand());
    }
}
