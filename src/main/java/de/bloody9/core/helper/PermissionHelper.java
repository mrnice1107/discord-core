package de.bloody9.core.helper;

import de.bloody9.core.permissions.GuildPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.bloody9.core.logging.Logger.*;

public class PermissionHelper {

    public static boolean equalsPermission(@NotNull String permission, @NotNull String toCheck) {
        debug("toCheck: " + toCheck);
        debug("permission: " + permission);

        if (toCheck.equals(".*")) return true;

        if (toCheck.startsWith(permission)) return true;

        if (toCheck.endsWith(".*")) {
            debug("toCheck endsWith .*, checking if matches with permission");
            toCheck = toCheck.replace(".*", "");
            return permission.startsWith(toCheck);
        }

        return false;
    }

    public static boolean memberHasPermission(@NotNull String permission, @Nullable Member member) {
        return memberHasPermission(permission, member, true);
    }

    public static boolean memberHasPermission(@NotNull String permission, @Nullable Member member, boolean messageUser) {
        if (member == null) {
            error("Failed to get permission because member is null!");
            return false;
        }

        debug("check if member has permission");
        debug("permission: " + permission);
        debug("member: " + member.getUser().getName());

        if (Helper.isOwner(member)) {
            debug("member is owner, ignoring check");
            return true;
        }

        Guild guild = member.getGuild();

        GuildPermission guildPermission = getGuildPermissionByGuild(guild.getId());

        if (guildPermission.memberHasPermission(permission, member)) {
            return true;
        } else {
            if (messageUser) Helper.sendPrivateMessage(member, "You don't enough permission!");
            debug("member don't have permission!");
            return false;
        }
    }

    public static boolean roleHasPermission(@NotNull String permission, @Nullable Role role) {
        if (role == null) {
            error("Failed to get permission because role is null!");
            return false;
        }

        debug("check if role has permission");
        debug("permission: " + permission);
        debug("role: " + role.getName());

        Guild guild = role.getGuild();

        GuildPermission guildPermission = getGuildPermissionByGuild(guild.getId());

        return guildPermission.roleHasPermission(permission, role);
    }

    public static boolean rolesHasPermission(@NotNull String permission, @NotNull List<Role> roles) {
        debug("check if role has permission");
        debug("permission: " + permission);
        if (roles.isEmpty()) {
            return false;
        }
        Guild guild = roles.get(0).getGuild();
        GuildPermission guildPermission = getGuildPermissionByGuild(guild.getId());

        return guildPermission.rolesHasPermission(permission, roles);
    }

    private static GuildPermission getGuildPermissionByGuild(@NotNull String guildId) {
        return GuildPermission.getGuildPermissionByID(guildId);
    }
}
