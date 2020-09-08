package de.bloody9.core.threads;

import de.bloody9.core.Bot;
import de.bloody9.core.models.interfaces.SimpleCommand;
import net.dv8tion.jda.api.JDA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import static de.bloody9.core.logging.Logger.*;

public class ConsoleCommandReader extends Thread {
    public final HashMap<String, SimpleCommand> commands;

    public ConsoleCommandReader(HashMap<String, SimpleCommand> commands) {
        this.commands = commands;
    }

    @Override
    public synchronized void start() {
        debug("starting console reader: to receive console commands while system is running");

        String help = "BaseCommands:\n" +
                "Use 'exit' to shutdown\n" +
                "Use 'update' to reload database\n" +
                "Use 'help' for a list with the other commands\n" +
                "Use 'loglevel log/error/warn/info/debug' to set the loglevel of the logger";

        String line;
        debug("get reader");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            debug("start loop to get console commands");
            Bot bot = Bot.INSTANCE;
            while ((line = reader.readLine()) != null && bot.isRunning()) {
                String key = line.toLowerCase();
                if (key.equals("exit")) {
                    debug("shutting down bot!");
                    JDA jda = bot.getJda();
                    if (jda != null) {

                        jda.shutdown();
                        info("-----------------");
                        info("Shutting down bot");
                        info("-----------------");

                        bot.setRunning(false);
                    }
                    reader.close();
                    break;
                } else if (key.equals("help")) {
                    info(commands.keySet().toString());
                } else {
                    String[] args = key.trim().split(" ");

                    if (args.length > 0) {
                        String command = args[0];

                        if (commands.containsKey(command)) {
                            SimpleCommand cmd = commands.get(command);

                            args = Arrays.copyOfRange(args, 1, args.length);
                            if (!cmd.perform(command, args)) {
                                info(help);
                            }
                        } else {
                            info(help);
                        }
                    } else {
                        info(help);
                    }
                }
            }
        } catch (IOException e) {
            error(e);
        }

        debug("ending console command reader");
    }
}
