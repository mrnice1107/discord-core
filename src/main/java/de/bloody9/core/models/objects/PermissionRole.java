package de.bloody9.core.models.objects;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.helper.PermissionHelper;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionRole extends GuildObject {

    private static final String TABLE = "guild_permissions";
    private static final String PERMISSIONS = "permission";
    private static final String GUILD_ID = "guild_id";
    private static final String MEMBER_ID = "member_id";

    private final Role role;
    private Set<String> permissions;

    public PermissionRole(@NotNull Role role) {
        super(role.getGuild());
        this.role = role;

        permissions = new HashSet<>();

        load();
    }

    public void load() {
        String query = GUILD_ID + "=" + getGuildId() + " AND " + MEMBER_ID + "=" + role.getId();
        permissions = new HashSet<>(Helper.getObjectFromDB(PERMISSIONS, TABLE, query));
    }


    public boolean hasPermission(String permission) {
        return permissions.stream().anyMatch(permKey -> PermissionHelper.equalsPermission(permission, permKey));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Role: @").append(role.getName());
        if (permissions == null || permissions.isEmpty()) {
            builder.append("\n").append("- No permissions found");
        } else {
            permissions.forEach(s -> builder.append("\n\"").append(s.toLowerCase()).append('"'));
        }
        return builder.toString();
    }

    public Role getRole() {
        return role;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public PermissionRole setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }
}
