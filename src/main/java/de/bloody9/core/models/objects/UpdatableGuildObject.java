package de.bloody9.core.models.objects;

import net.dv8tion.jda.api.entities.Guild;

public abstract class UpdatableGuildObject extends GuildObject {

    public UpdatableGuildObject(Guild guild) {
        super(guild);
    }

    public abstract void update();
}
