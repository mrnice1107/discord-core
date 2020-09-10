package de.bloody9.core.models.interfaces;

import de.bloody9.core.models.objects.UpdatableGuildObject;

import java.util.List;

public interface ConfigUpdater {

    UpdatableGuildObject getGuildConfigByGuildID(String guildId);
    List<? extends UpdatableGuildObject> getGuildAllConfigs();

}
