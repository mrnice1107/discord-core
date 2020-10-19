package de.bloody9.core.exceptions.Command;

public class WrongArgumentCommandException extends BotCommandException {
    public WrongArgumentCommandException() {
        super("Wrong arguments");
    }
}
