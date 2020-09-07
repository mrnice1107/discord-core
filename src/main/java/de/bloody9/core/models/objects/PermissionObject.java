package de.bloody9.core.models.objects;

public class PermissionObject {

    private String permission;
    private String description;

    public PermissionObject(String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public PermissionObject setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PermissionObject setDescription(String description) {
        this.description = description;
        return this;
    }
}
