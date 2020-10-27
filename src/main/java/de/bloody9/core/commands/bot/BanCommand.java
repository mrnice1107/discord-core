package de.bloody9.core.commands.bot;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.exceptions.Mentioned.NoMentionedMembersCommandException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.GuildObject;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class BanCommand implements BotCommand {

    private static final String generalPermission = "commands.ban";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;

    public BanCommand() {

        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute ban command"));

        aliases = new ArrayList<>();
        aliases.add("banid");

        description = "With this command you can ban members from your discord";

        help = "Ban Command\n" +
                "<prefix> ban <@Member> [<reason>]\n" +
                "<prefix> banid <userId> [<reason>]";
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, getHelp());
    }

    @Override
    public String getHelp() {
        return Helper.constructHelp(help);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }

    @Override
    public List<String> getAlias() {
        return aliases;
    }

    // if return true the initial command message will be removed
    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start BanCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        debug("checking args.length > 0");
        if (args.length <= 0) {
            sendHelp(sender);

            throw new NotEnoughArgumentCommandException(args.length);
        }

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        Guild guild = message.getGuild();

        if (command.equalsIgnoreCase("banid")) {
            return banId(sender, args, guild);
        }

        debug("check for mentioned members");
        if (message.getMentionedMembers().isEmpty()) {
            Helper.sendPrivateMessage(sender, "You need to mention a member to ban!");
            throw new NoMentionedMembersCommandException();
        }

        Member banMember = message.getMentionedMembers().get(0);
        String reason = getReason(args);

        User banUser = banMember.getUser();

        Helper.sendPrivateMessage(banUser, "You got banned from " + guild.getName() + " reason:\n" + reason);
        Helper.sendPrivateMessage(sender, "You banned " + banUser.getAsTag() + " from " + guild.getName() + " reason:\n" + reason);

        new GuildObject(guild).modLog("The member " + banUser.getAsTag() + " got banned, reason:\n" + reason,
                getEmbed(banUser.getAsMention(), sender.getAsTag(), reason));

        banMember.ban(0, reason).queue();

        return true;
    }

    private String getReason(String[] args) {
        if (args.length > 1) {
            StringJoiner joiner = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                joiner.add(args[i]);
            }
            return joiner.toString();
        }
        return "none";
    }

    private boolean banId(User sender, String[] args, Guild guild) {
        String banUserId = args[0];

        try {
            Long.parseLong(banUserId);
            if (banUserId.length() != 18) throw new NumberFormatException();
        } catch(NumberFormatException e){
            Helper.sendPrivateMessage(sender, "Invalid argument, *" + args[0] + "* is no valid user id!");
            throw new WrongArgumentCommandException();
        }

        final String TABLE = "guild_bans";
        final String MEMBER_ID = "member_id";
        final String GUILD_ID = "guild_id";

        final String contentType = MEMBER_ID + "," + GUILD_ID;
        final String content = banUserId + "," + guild.getId();

        Helper.executeInsertSQL(TABLE, contentType, content);

        String reason = getReason(args);

        Helper.sendPrivateMessage(sender, "You banned " + banUserId + " from " + guild.getName() + " reason:\n" + reason);
        new GuildObject(guild).modLog("The user " + banUserId + " got id banned, reason:\n" + reason,
                getEmbed(Helper.getIdAsMentioned(banUserId), sender.getAsTag(), reason));

        return true;
    }

    private EmbedBuilder getEmbed(String banned, String banner, String reason) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(0xde1a10);
        builder.setTitle("Ban");
        builder.setDescription("The user: " + banned + " got banned\n" +
                "reason: " + reason);
        builder.setFooter("banned by " + banner);

        return builder;
    }

}