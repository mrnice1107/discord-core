package de.bloody9.core.listener;

import static de.bloody9.core.logging.Logger.*;

import de.bloody9.core.Bot;
import de.bloody9.core.commands.CommandManager;
import de.bloody9.core.helper.Helper;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Arrays;


public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT) && !event.getAuthor().isBot()) { // only server text channel
            final String messageAsSting = event.getMessage().getContentDisplay(); // received message (with formatting's and stuff)
            final String prefix = Helper.getCommandPrefix();
            if (messageAsSting.toLowerCase().startsWith(prefix)) {
                String[] args = messageAsSting.substring(prefix.length()).trim().split(" ");

                TextChannel channel = event.getTextChannel();
                Message message = event.getMessage();
                User sender = event.getAuthor();

                CommandManager commandManager = Bot.INSTANCE.getCommandManager();

                if (args.length > 0) {
                    String command = args[0];
                    args = Arrays.copyOfRange(args, 1, args.length);
                    if (!commandManager.performCommand(command, sender, message, args)) {
                        warn("Something went wrong by executing the command: " + command
                                + ", with the arguments: " + Arrays.toString(args)
                                + ", in the channel: " + channel.getAsMention()
                                + ", send form: " + sender.getAsMention());
                    }
                } else {
                    commandManager.performCommand("help", sender, message, args);
                }
            }
        }
    }
}
