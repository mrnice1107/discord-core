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


public class KickCommand implements BotCommand {

    private static final String generalPermission = "commands.kick";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;
    private final String description;

    private final String help;

    public KickCommand() {

        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute kick command"));

        aliases = new ArrayList<>();

        description = "With this command you can kick members from your discord";

        help = "Kick Command\n" +
                "<prefix> kick <@Member> [<reason>]";
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

        debug("start KickCommand");

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

        debug("check for mentioned members");
        if (message.getMentionedMembers().isEmpty()) {
            Helper.sendPrivateMessage(sender, "You need to mention a member to kick!");
            throw new NoMentionedMembersCommandException();
        }

        Member kickMember = message.getMentionedMembers().get(0);

        StringJoiner joiner = new StringJoiner(" ");

        String reason;
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                joiner.add(args[i]);
            }
            reason = joiner.toString();
        } else {
            reason = "none";
        }

        Helper.sendPrivateMessage(kickMember, "You got kicked from " + message.getGuild().getName() + " reason:\n" + reason);
        Helper.sendPrivateMessage(sender, "You kicked " + kickMember.getUser().getAsTag() + " from " + message.getGuild().getName() + " reason:\n" + reason);
        kickMember.kick(reason).queue();
        new GuildObject(message.getGuild()).modLog("The member " + kickMember.getUser().getAsTag() + " got kicked, reason:\n" + reason,
                getEmbed(kickMember.getAsMention(), sender.getAsTag(), reason));

        return true;
    }

    private EmbedBuilder getEmbed(String kickedMember, String sender, String reason) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(0xffea47);
        builder.setTitle("Kick");
        builder.setDescription("The user: " + kickedMember + " got kicked\n" +
                "reason: " + reason);
        builder.setFooter("kicked by " + sender);

        return builder;
    }

}