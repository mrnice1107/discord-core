package de.bloody9.core.exceptions.Command;

import de.bloody9.core.exceptions.BotException;
import org.jetbrains.annotations.NotNull;

public class BotCommandException extends BotException {

    public BotCommandException(@NotNull String message) {
        super(message);
    }
}
