package de.bloody9.core.exceptions.Feature;

import de.bloody9.core.exceptions.Command.BotCommandException;
import de.bloody9.core.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class FeatureCommandException extends BotCommandException {

    public FeatureCommandException(@NotNull String message) {
        super(message);
    }

    public FeatureCommandException(@NotNull Feature feature) {
        super("An error occurred in the feature: " + feature.toString());
    }

    public FeatureCommandException(@NotNull Feature feature, @NotNull String message) {
        super(feature.toString() + ":" + message);
    }
}
