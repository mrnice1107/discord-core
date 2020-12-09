package de.bloody9.core.exceptions.Feature;

import de.bloody9.core.exceptions.BotException;
import de.bloody9.core.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class FeatureException extends BotException {
    public FeatureException(@NotNull String message) {
        super(message);
    }

    public FeatureException(@NotNull Feature feature) {
        super("An error occurred in the feature: " + feature.toString());
    }

    public FeatureException(@NotNull Feature feature, @NotNull String message) {
        super(feature.toString() + ":" + message);
    }
}
