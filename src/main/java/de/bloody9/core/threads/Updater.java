package de.bloody9.core.threads;

// logging
import static de.bloody9.core.logging.Logger.*;

import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.ConfigObject;
import de.bloody9.core.models.interfaces.ConfigUpdater;

import java.util.List;


public class Updater extends Thread {
    private final Bot instance;
    private Integer counter;

    private final List<ConfigUpdater> updater;

    public Updater(List<ConfigUpdater> updater) {
        instance = Bot.INSTANCE;
        counter = 0;

        this.updater = updater;
    }

    public void run() {
        info("Updater started");
        counter = 9*60;
        debug("starting while loop");
        final long sleepTime = 5*1000L;
        while (instance.isRunning()) {
            if (counter >= 10*60) { // 10 min
                debug("counter is high enough to update");
                update();
            }

            try {
                //noinspection BusyWait
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                error(e);
                debug("automatically break the loop!");
                break;
            }

            counter += (int) sleepTime/1000; // divide the sleep time with 1000 to get the passed seconds
        }
        info("Updater stopped");
    }

    public synchronized void update() {
        info("Updating");
        updateConfigs();
        updateOthers();
    }

    public synchronized void updateConfigs() {
        debug("setting counter: 0");
        counter = 0;

        debug("foreach every loaded guild and init ConfigObjects");
        instance.getJda().getGuilds().forEach(guild -> updater.forEach(up -> up.getGuildConfigByGuildID(guild.getId())));

        debug("Reloading all loaded ConfigObjects");
        updater.forEach(up -> up.getGuildAllConfigs().forEach(ConfigObject::load));
    }

    public synchronized void updateOthers() {
        debug("doing other updates");
    }

}