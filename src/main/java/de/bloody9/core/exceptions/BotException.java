package de.bloody9.core.exceptions;

import org.jetbrains.annotations.NotNull;

public class BotException extends RuntimeException {
    public BotException(@NotNull String message) {
        super(message);
    }
}