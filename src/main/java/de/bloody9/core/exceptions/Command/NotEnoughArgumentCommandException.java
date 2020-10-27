package de.bloody9.core.exceptions.Command;

public class NotEnoughArgumentCommandException extends BotCommandException {
    public NotEnoughArgumentCommandException(int argLength) {
        super("To less arguments: length = " + argLength);
    }
}
