package de.bloody9.core.models.interfaces;

import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;


public interface BotCommand {

    /**
     * @return The return value of the command says if the initial command message should be removed or not!
     * true = remove message,
     * false = not remove message
     */
    boolean performCommand(String command, User sender, Message message, String[] args);

    String getHelp();
    String getDescription();
    List<PermissionObject> getPermissions();

    List<String> getAlias();
}
