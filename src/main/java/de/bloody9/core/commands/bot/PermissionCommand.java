package de.bloody9.core.commands.bot;

import de.bloody9.core.Bot;
import de.bloody9.core.config.GuildPermission;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

import static de.bloody9.core.helper.Helper.hasPermission;
import static de.bloody9.core.logging.Logger.debug;

public class PermissionCommand implements BotCommand {

    private final String generalPermission = "permissions.manage";
    private final String addPermission = "permissions.manage.add";
    private final String removePermission = "permissions.manage.remove";
    private final String getPermission = "permissions.manage.get";

    private final List<PermissionObject> permissionObjects;
    private final String description;

    private final String help;

    public PermissionCommand() {
        String prefix = Bot.INSTANCE.getCommandPrefix();
        permissionObjects = new ArrayList<>();

        permissionObjects.add(new PermissionObject(generalPermission, "Execute command"));
        permissionObjects.add(new PermissionObject(addPermission, "Add permission to users and roles"));
        permissionObjects.add(new PermissionObject(removePermission, "Remove permission from users and roles"));
        permissionObjects.add(new PermissionObject(getPermission, "Get list of permissions"));

        description = "With this command you can manage the Permissions of the Bot\n" +
                "Every command needs a permission to execute it, with this command you can give or take the them to or from users and roles";

        help = "Permission Command\n"
                + prefix + " permission add/remove <permission> <@Role/@Member>\n"
                + prefix + " permission get";
    }

    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }

    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start guildCommand");

        debug("command: " + command);
        debug("sender: " + sender.getName() + ":" + sender.getId());
        debug("message: " + message.getContentRaw());
        debug("args: " + Arrays.toString(args));

        if (!hasPermission(generalPermission, message.getMember())) {
            return false;
        }

        debug("checking args.length == 1: " + args.length + "&& args[0] == get");
        if (args.length == 1 && args[0].equalsIgnoreCase("get")) {
            if (!hasPermission(getPermission, message.getMember())) {
                return false;
            }
            debug("getting permissions");

            StringBuilder builder = new StringBuilder();

            Bot.INSTANCE.getCommandManager().getCommands().forEach((s, cmd) ->
                    cmd.getPermissions().forEach(perm ->
                            builder.append(perm.getPermission()).append(": ").append(perm.getDescription()).append("\n")
                    )
            );
            String perms = builder.toString();

            debug("permissions: " + perms);

            Helper.sendPrivateMessage(sender, "Permission list: (permission:description)\n" + perms);

            return true;
        }

        debug("checking args.length < 3: " + args.length);
        if (args.length < 3) {
            debug("args to small, stop perform (send help to user)");
            Helper.sendPrivateMessage(sender, help);
            return false;
        }
        debug("args not to small, continue");

        debug("check args 0 == add or remove: " + args[0]);
        if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
            debug("args wrong (send help to user)");
            Helper.sendPrivateMessage(sender, help);
            return false;
        }
        debug("args 0: okay");
        debug("init: get guild, guildPermission, members set (new empty set)");

        Guild guild = message.getGuild();
        GuildPermission guildPermission = GuildPermission.getGuildPermissionByID(guild.getId());
        Set<String> permMembers = new HashSet <>();

        debug("foreach all mentioned members and add them to members set");
        StringBuilder memberBuilder = new StringBuilder();
        for (Member mentionedMember : message.getMentionedMembers()) {
            memberBuilder.append("@").append(mentionedMember.getUser().getAsTag()).append(", ");
            permMembers.add(mentionedMember.getId());
        }

        debug("foreach all mentioned roles and add them to members set");
        for (Role mentionedRole : message.getMentionedRoles()) {
            permMembers.add(mentionedRole.getId());
            memberBuilder.append("@").append(mentionedRole.getName()).append(", ");
        }

        debug("memberBuilder (list of members): " + memberBuilder.toString());
        if (memberBuilder.length() > 0) {
            memberBuilder.setLength(memberBuilder.length() - 2);
            debug("memberBuilder (list of members): " + memberBuilder.toString());
        }

        debug(permMembers.toString());


        String permission = args[1];
        debug("to edit permission: " + permission);

        if (args[0].equalsIgnoreCase("add")) {
            debug("operation add");
            if (hasPermission(addPermission, message.getMember())) {
                debug("add members set to guildPermission");
                guildPermission.addMember(permMembers, permission);

                Helper.sendPrivateMessage(sender, "You successfully added the permission *" + permission + "* to: " + memberBuilder.toString());
            }
        } else if (args[0].equalsIgnoreCase("remove")){
            debug("operation remove");
            if (hasPermission(removePermission, message.getMember())) {
                debug("remove members set from guildPermission");
                guildPermission.removeMember(permMembers, permission);

                Helper.sendPrivateMessage(sender, "You successfully removed the permission *" + permission + "* from: " + memberBuilder.toString());
            }
        }

        return true;
    }
}
