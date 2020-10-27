package de.bloody9.core.exceptions.Mentioned;

public class NoMentionedChannelsCommandException extends NoMentionedCommandException {
    public NoMentionedChannelsCommandException() {
        super("channels");
    }
}
