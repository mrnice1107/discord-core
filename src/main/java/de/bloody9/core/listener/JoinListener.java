package de.bloody9.core.listener;

import de.bloody9.core.helper.Helper;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends ListenerAdapter {

    private static final String TABLE = "guild_bans";
    private static final String MEMBER_ID = "member_id";
    private static final String GUILD_ID = "guild_id";

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        final String query = GUILD_ID + "=" + event.getGuild().getId() + " AND " + MEMBER_ID + "=" + event.getMember().getId();

        if (Helper.getFirstObjectFromDB(MEMBER_ID, TABLE, query) != null) {
            event.getMember().ban(0, "This member was already id banned!").queue();
            Helper.executeDeleteSQL(TABLE, query);
        }
    }
}
