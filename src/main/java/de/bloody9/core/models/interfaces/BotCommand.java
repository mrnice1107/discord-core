package de.bloody9.core.models.interfaces;

import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;


public interface BotCommand {

    boolean performCommand(String command, User sender, Message message, String[] args);

    String getHelp();
    String getDescription();
    List<PermissionObject> getPermissions();
}
