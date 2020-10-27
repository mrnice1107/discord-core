package de.bloody9.core.exceptions.Mentioned;

public class NoMentionedRolesCommandException extends NoMentionedCommandException {
    public NoMentionedRolesCommandException() {
        super("roles");
    }
}
