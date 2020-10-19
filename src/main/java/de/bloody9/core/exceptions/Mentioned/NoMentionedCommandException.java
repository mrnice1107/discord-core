package de.bloody9.core.exceptions.Mentioned;

import de.bloody9.core.exceptions.Command.BotCommandException;
import org.jetbrains.annotations.NotNull;

public class NoMentionedCommandException extends BotCommandException {
    public NoMentionedCommandException() {
        super("No mentioned");
    }

    public NoMentionedCommandException(@NotNull String type) {
        super("No mentioned " + type);
    }
}
