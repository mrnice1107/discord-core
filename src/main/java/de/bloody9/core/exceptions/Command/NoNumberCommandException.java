package de.bloody9.core.exceptions.Command;

public class NoNumberCommandException extends BotCommandException {
    public NoNumberCommandException() {
        super("Argument must be number");
    }
}
