package de.bloody9.core.exceptions.Mentioned;

public class NoMentionedMembersCommandException extends NoMentionedCommandException {
    public NoMentionedMembersCommandException() {
        super("members");
    }
}
