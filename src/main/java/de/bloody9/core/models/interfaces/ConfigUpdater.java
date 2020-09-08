package de.bloody9.core.models.interfaces;

import de.bloody9.core.models.objects.ConfigObject;

import java.util.List;

public interface ConfigUpdater {

    ConfigObject getGuildConfigByGuildID(String guildId);
    List<? extends ConfigObject> getGuildAllConfigs();

}
