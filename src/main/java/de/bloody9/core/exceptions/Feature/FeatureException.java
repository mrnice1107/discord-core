package de.bloody9.core.exceptions.Feature;

import de.bloody9.core.exceptions.BotException;
import org.jetbrains.annotations.NotNull;

public class FeatureException extends BotException {
    public FeatureException(@NotNull String message) {
        super(message);
    }
}
