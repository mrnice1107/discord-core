package de.bloody9.core.config;

import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.ConfigObject;
import de.bloody9.core.models.interfaces.ConfigUpdater;

import java.util.List;

public class GuildPermissionUpdater implements ConfigUpdater {

    private final Bot instance;

    public GuildPermissionUpdater() {
        instance = Bot.INSTANCE;
    }


    @Override
    public ConfigObject getGuildConfigByGuildID(String guildId) {
        return GuildPermission.getGuildPermissionByID(guildId);
    }

    @Override
    public List<ConfigObject> getGuildAllConfigs() {
        List<?> guildPermissionList = instance.getGuildPermissions();

        return (List<ConfigObject>)guildPermissionList;
    }
}
