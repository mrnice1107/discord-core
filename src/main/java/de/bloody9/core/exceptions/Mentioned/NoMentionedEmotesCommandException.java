package de.bloody9.core.exceptions.Mentioned;

public class NoMentionedEmotesCommandException extends NoMentionedCommandException {
    public NoMentionedEmotesCommandException() {
        super("emotes");
    }
}
