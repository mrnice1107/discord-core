package de.bloody9.core.permissions;

import de.bloody9.core.Bot;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.objects.PermissionRole;
import de.bloody9.core.models.objects.PermissionUser;
import de.bloody9.core.models.objects.UpdatableGuildObject;
import de.bloody9.core.mysql.MySQLConnection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GuildPermission extends UpdatableGuildObject {

    @Deprecated
    private Map<String, Set<String>> permissions;
    //<permission -> <userIds>>

    private final Map<String, PermissionUser> permissionUsers;
    //<userId, permissionUserObject>

    private final Map<String, PermissionRole> permissionRoles;
    //<roleId, PermissionRoleObject>

    public static GuildPermission getGuildPermissionByID(String guildID) {
        Bot bot = Bot.INSTANCE;
        for (GuildPermission guildPermission : bot.getGuildPermissions()) {
            if (guildPermission.getGuild().getId().equals(guildID)) {
                return guildPermission;
            }
        }

        Guild guild = bot.getJda().getGuildById(guildID);
        GuildPermission guildPermission = new GuildPermission(guild);
        bot.addGuildPermission(guildPermission);
        return guildPermission;
    }

    private GuildPermission(Guild guild) {
        super(guild);

        permissionUsers = new HashMap<>();
        permissionRoles = new HashMap<>();

        update();
    }

    @Override
    public void update() {
        info("Loading GuildPermission");

        load();
        loadOld();

        info("Loaded GuildPermission");
    }

    private void load() {
        permissionUsers.values().forEach(PermissionUser::load);
        permissionRoles.values().forEach(PermissionRole::load);
    }

    @Deprecated
    private void loadOld() {

        permissions = new HashMap<>();
        debug("load old stuff");
        for (String permissionFromDB : Helper.getObjectFromDB("permission", "guild_permissions", "guild_id=" + getGuildId())) {
            Set<String> members = new HashSet<>(Helper.getObjectFromDB("member_id", "guild_permissions", "permission='" + permissionFromDB + "'"));
            debug("loading permission: " + permissionFromDB + ", for members: " + members.toString());
            permissions.put(permissionFromDB, members);
        }

    }

    public void addPermissionIDs(Set<String> ids, String permission) {
        if (ids == null || ids.isEmpty() || permission == null ) {
            return;
        }
        Set<String> updates = new HashSet<>();

        debug("adding permission: " + permission + ", to ids: " + ids.toString());

        for (String member : ids) {
            String update = Helper.getInsertSQL("guild_permissions", "guild_id, member_id, permission", getGuildId() + "," + member + ",'" + permission+"'");
            updates.add(update);
        }

        if (!MySQLConnection.executeUpdate(updates)) {
            error("Failed to insert permissions to DB");
            return;
        }
        debug("Permissions added to DB");
        debug("Permissions successfully added");
        update();
    }

    public void removePermissionIDs(Set<String> ids, String permission) {
        if (ids == null || permission == null || permission.equals("")){
            return;
        }

        Set<String> updates = new HashSet<>();

        debug("removing permission: " + permission + ", to ids: " + ids.toString());

        for (String member : ids) {
            String update = Helper.getDeleteSQL("guild_permissions", "member_id=" + member + " AND permission='" + permission + "'");
            updates.add(update);
        }

        if (!MySQLConnection.executeUpdate(updates)) {
            error("Failed to delete permissions from DB");
            return;
        }

        debug("Permissions delete to DB");
        debug("Permissions successfully removed");
        update();
    }

    @Deprecated
    public void addMember(Set<String> members, String permission) {
        if (members == null || members.isEmpty() || permission == null ) {
            return;
        }
        Set<String> updates = new HashSet<>();

        debug("adding permission: " + permission + ", to members: " + members.toString());

        for (String member : members) {
            String update = Helper.getInsertSQL("guild_permissions", "guild_id, member_id, permission", getGuildId() + "," + member + ",'" + permission+"'");
            updates.add(update);
        }

        if (!MySQLConnection.executeUpdate(updates)) {
            error("Failed to insert permissions to DB");
            return;
        }
        debug("Permissions added to DB");

        Set<String> oldMembers;
        if (permissions.containsKey(permission)) {
            oldMembers = new HashSet<>(permissions.get(permission));
            oldMembers.addAll(members);
        } else {
            oldMembers = members;
        }
        permissions.put(permission, oldMembers);
        debug("Permissions successfully added");
    }

    @Deprecated
    public void removeMember(Set<String> members, String permission) {
        if (members == null || permission == null || permission.equals("")){
            return;
        }

        Set<String> updates = new HashSet<>();

        debug("removing permission: " + permission + ", to members: " + members.toString());

        for (String member : members) {
            String update = Helper.getDeleteSQL("guild_permissions", "member_id=" + member + " AND permission='" + permission + "'");
            updates.add(update);
        }

        if (!MySQLConnection.executeUpdate(updates)) {
            error("Failed to delete permissions from DB");
            return;
        }
        debug("Permissions delete to DB");

        if (permissions.containsKey(permission)) {
            Set<String> oldMembers = new HashSet<>(permissions.get(permission));
            oldMembers.removeAll(members);
            if (oldMembers.isEmpty()) {
                permissions.remove(permission);
            } else {
                permissions.put(permission, oldMembers);
            }
        }
        debug("Permissions successfully removed");
    }

    public boolean memberHasPermission(@NotNull String permission, @NotNull Member member) {
        String userId = member.getId();
        PermissionUser permissionUser = getPermissionUser(member);
        if (permissionUser == null) {
            error("failed to get permissionUser from member!");
            return false;
        }
        permissionUsers.put(userId, permissionUser);
        return permissionUser.hasPermission(permission);
    }

    public PermissionUser getPermissionUser(@NotNull Member member) {
        String userId = member.getId();
        return (permissionUsers.containsKey(userId)) ? permissionUsers.get(userId) : new PermissionUser(member);
    }

    public boolean roleHasPermission(@NotNull String permission, @NotNull Role role) {
        String roleId = role.getId();
        PermissionRole permissionRole = getPermissionRole(role);
        if (permissionRole == null) {
            error("failed to get permissionRole from role!");
            return false;
        }
        permissionRoles.put(roleId, permissionRole);
        return permissionRole.hasPermission(permission);
    }
    public boolean rolesHasPermission(@NotNull String permission, List<Role> roles) {
        return roles.stream().anyMatch(role -> roleHasPermission(permission, role));
    }

    public PermissionRole getPermissionRole(@NotNull Role role) {
        String userId = role.getId();
        return (permissionRoles.containsKey(userId)) ? permissionRoles.get(userId) : new PermissionRole(role);
    }


    @Deprecated
    public boolean hasPermission(String permission, Member guildMember) {
        if (permission == null || guildMember == null)  {
            return false;
        }
        debug("checking if member: " + guildMember.getUser().getName() + " has permission: " + permission);

        if (hasPermission(permission, guildMember.getId())) {
            return true;
        }
        for (Role role : guildMember.getRoles()) {
            if (hasPermission(permission, role)) {
                return true;
            }
        }

        return false;
    }

    @Deprecated
    public boolean hasPermission(String permission, Role role) {
        return hasPermission(permission, role.getId());
    }

    @Deprecated
    public boolean hasPermission(String permission, String toCheckID) {
        if (permission == null || toCheckID == null) {
            return false;
        }
        permission = permission.toLowerCase();

        debug("checking has permission: " + permission + ", user: " + toCheckID);

        if (permissions.containsKey(permission)) {
            debug("checking direct permission");
            boolean hasPerm = containsToCheck(permission, toCheckID);
            debug("direct permission: " + hasPerm);
            if (hasPerm) {
                return true;
            }
        }


        debug("checking indirect permission");
        for (String permKey : permissions.keySet()) {
            debug("permKey: " + permKey);
            debug("checking if permission: " + permission + " is sub permission of permKey");

            if (permKey.startsWith(permission) && containsToCheck(permKey, toCheckID)) {
                debug("member has permission: " + true);
                return true;
            }

            if (permKey.endsWith(".*")) {
                debug("permKey endsWith .*, checking if matches with permission");
                permKey = permKey.replace(".*", "");
                if (permission.startsWith(permKey) && containsToCheck(permKey, toCheckID)) {
                    debug("member has permission: " + true);
                    return true;
                }
            }
        }

        debug("member has permission: " + false);
        return false;
    }

    @Deprecated
    private boolean containsToCheck(String permission, String toCheck) {
        if (permissions.containsKey(permission)) {
            return permissions.get(permission).contains(toCheck);
        } else return false;
    }

    @Deprecated
    public Set<String> getMemberPermissions(String id) {
        return permissions.get(id);
    }
}
