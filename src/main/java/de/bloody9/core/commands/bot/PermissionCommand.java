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

        debug("check args.length > 0");
        if (args.length == 0) {
            sendHelp(sender);
            return false;
        }

        debug("init: get guild, guildPermission");
        Guild guild = message.getGuild();
        GuildPermission guildPermission = GuildPermission.getGuildPermissionByID(guild.getId());

        guildPermission.debug("checking args.length == 1: " + args.length + "&& args[0] == get");
        if (args[0].equalsIgnoreCase("get")) {
            return get(args, message, sender, guildPermission);
        }

        guildPermission.debug("checking args.length < 3: " + args.length);
        if (args.length < 3) {
            guildPermission.debug("args to small, stop perform (send help to user)");
            sendHelp(sender);
            return false;
        }
        guildPermission.debug("args not to small, continue");

        guildPermission.debug("check args 0 == add or remove: " + args[0]);
        if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
            guildPermission.debug("args wrong (send help to user)");
            sendHelp(sender);
            return false;
        }
        guildPermission.debug("args 0: okay");
        guildPermission.debug("init: members set (new empty set)");

        Set<String> permMembers = new HashSet <>();

        guildPermission.debug("foreach all mentioned members and add them to members set");
        StringBuilder memberBuilder = new StringBuilder();
        for (Member mentionedMember : message.getMentionedMembers()) {
            memberBuilder.append("@").append(mentionedMember.getUser().getAsTag()).append(", ");
            permMembers.add(mentionedMember.getId());
        }

        guildPermission.debug("foreach all mentioned roles and add them to members set");
        for (Role mentionedRole : message.getMentionedRoles()) {
            permMembers.add(mentionedRole.getId());
            memberBuilder.append("@").append(mentionedRole.getName()).append(", ");
        }

        guildPermission.debug("memberBuilder (list of members): " + memberBuilder.toString());
        if (memberBuilder.length() > 0) {
            memberBuilder.setLength(memberBuilder.length() - 2);
            guildPermission.debug("memberBuilder (list of members): " + memberBuilder.toString());
        }

        guildPermission.debug(permMembers.toString());


        String permission = args[1];
        guildPermission.debug("to edit permission: " + permission);

        if (args[0].equalsIgnoreCase("add")) {
            guildPermission.debug("operation add");
            if (hasPermission(addPermission, message.getMember())) {
                guildPermission.debug("add members set to guildPermission");
                guildPermission.addMember(permMembers, permission);

                Helper.sendPrivateMessage(sender, "You successfully added the permission *" + permission + "* to: " + memberBuilder.toString());
                guildPermission.modLog(sender.getAsMention() + ": added the permission *" + permission + "* to: " + memberBuilder.toString());
            }
        } else if (args[0].equalsIgnoreCase("remove")){
            guildPermission.debug("operation remove");
            if (hasPermission(removePermission, message.getMember())) {
                guildPermission.debug("remove members set from guildPermission");
                guildPermission.removeMember(permMembers, permission);

                Helper.sendPrivateMessage(sender, "You successfully removed the permission *" + permission + "* from: " + memberBuilder.toString());
                guildPermission.modLog(sender.getAsMention() + ": removed the permission *" + permission + "* from: " + memberBuilder.toString());
            }
        }

        return true;
    }

    private boolean get(String[] args, Message message, User sender, GuildPermission guildPermission) {
        if (!hasPermission(getPermission, message.getMember())) {
            return false;
        }

        if (args.length < 2) {
            guildPermission.debug("getting permissions");

            StringBuilder builder = new StringBuilder();
            guildPermission.debug("collecting permissions and add them to string list");
            getAllPermissions().forEach(perm -> addPermission(perm, builder, guildPermission));

            String perms = builder.toString();
            guildPermission.debug("permissions: " + perms);
            Helper.sendPrivateMessage(sender, "Permission list: (permission key: description)\n" + perms);

            return true;
        }

        guildPermission.debug("initializing builder");
        StringBuilder builder = new StringBuilder();

        guildPermission.debug("check for mentioned members");
        if (!message.getMentionedMembers().isEmpty()) {
            guildPermission.debug("adding mentioned members");
            addPermissionMembers(message.getMentionedMembers(), builder, guildPermission);
        }
        guildPermission.debug("check for mentioned roles");
        if (!message.getMentionedRoles().isEmpty()) {
            guildPermission.debug("adding mentioned roles");
            addPermissionRoles(message.getMentionedRoles(), builder, guildPermission);
        }

        String result = builder.toString().trim();
        if (result.equals("")) {
            guildPermission.debug("builder is empty -> no mentioned roles or members found");
            guildPermission.debug("adding permissions with descriptions");
            for (int i = 1; i < args.length; i++) {
                String permission = args[i];
                getAllPermissions().stream().filter(perm -> perm.getPermission().equals(permission)).forEach(perm -> addPermission(perm, builder, guildPermission));
            }
            result = builder.toString().trim();
        }
        Helper.sendPrivateMessage(sender, result);
        return true;
    }


    private void addPermissionMembers(List<Member> members, StringBuilder builder, GuildPermission guildPermission) {
        members.forEach(member -> {
            builder.append("\nMember: @").append(member.getUser().getName());
            builder.append("\nRawPermissions: ");
            addMemberPermissions(member.getId(), builder, guildPermission);

            addPermissionRoles(member.getRoles(), builder, guildPermission);
        });
    }

    private void addPermissionRoles(List<Role> roles, StringBuilder builder, GuildPermission guildPermission) {
        roles.forEach(role -> {
            builder.append("\nRole: @").append(role.getName());
            builder.append("\nRolePermissions: ");

            addMemberPermissions(role.getId(), builder, guildPermission);
        });
    }

    private void addMemberPermissions(String id, StringBuilder builder, GuildPermission guildPermission) {
        guildPermission.getMemberPermissions(id).forEach(s -> builder.append("\n- ").append(s.toLowerCase()));
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, help);
    }

    private void addPermission(PermissionObject permission, StringBuilder builder, GuildPermission guildPermission) {
        guildPermission.debug("adding permission: " + permission.toString());
        builder.append(permission.toString()).append("\n");
    }

    public List<PermissionObject> getAllPermissions() {
        List<PermissionObject> permissions = new ArrayList<>();
        Bot.INSTANCE.getCommandManager().getCommands().values().forEach(cmd -> permissions.addAll(cmd.getPermissions()));
        permissions.addAll(Bot.INSTANCE.getPermissions());

        return permissions;
    }
}
