package de.bloody9.core.exceptions.Command;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class NoPermissionCommandException extends BotCommandException {
    public NoPermissionCommandException(@NotNull User sender, @NotNull String permission) {
        super("No permission: " + sender.getAsTag() + " -> " + permission);
    }
    public NoPermissionCommandException(@NotNull String permission, @NotNull User sender) {
        this(sender, permission);
    }
}
