package de.bloody9.core.models.objects;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.helper.PermissionHelper;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionUser extends GuildObject {

    private static final String TABLE = "guild_permissions";
    private static final String PERMISSIONS = "permission";
    private static final String GUILD_ID = "guild_id";
    private static final String MEMBER_ID = "member_id";

    private final Member member;

    private Set<String> permissions;


    public PermissionUser(@NotNull Member member) {
        super(member.getGuild());
        this.member = member;

        permissions = new HashSet<>();

        load();
    }

    public void load() {
        String query = GUILD_ID + "=" + getGuildId() + " AND " + MEMBER_ID + "=" + member.getId();
        permissions = new HashSet<>(Helper.getObjectFromDB(PERMISSIONS, TABLE, query));
    }


    public boolean hasPermission(String permission) {
        if (permissions.stream().anyMatch(permKey -> PermissionHelper.equalsPermission(permission, permKey))) return true;
        return PermissionHelper.rolesHasPermission(permission, member.getRoles());
    }

    public Member getMember() {
        return member;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Member: @").append(member.getUser().getAsTag());
        if (permissions == null || permissions.isEmpty()) {
            builder.append("\n").append("- No permissions found");
        } else {
            permissions.forEach(s -> builder.append("\n\"").append(s.toLowerCase()).append('"'));
        }
        return builder.toString();
    }

    public PermissionUser setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }
}
