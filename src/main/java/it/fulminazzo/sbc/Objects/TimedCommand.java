package it.fulminazzo.sbc.Objects;

import it.fulminazzo.sbc.StrippedBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TimedCommand {
    private final int time;
    private final BukkitTask task;
    private final List<List<String>> commands;
    private List<String> recentlySentCommands;

    public TimedCommand(StrippedBroadcast plugin, String time, ConfigurationSection commandsSection) {
        this.time = Integer.parseInt(time);
        int[] timer = new int[]{this.time};
        commands = commandsSection.getKeys(false)
                .stream()
                .map(commandsSection::getStringList)
                .filter(l -> !l.isEmpty()).collect(Collectors.toList());
        recentlySentCommands = new ArrayList<>();
        if (commands.isEmpty()) this.task = null;
        else this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            timer[0]--;
            if (timer[0] == 0) {
                timer[0] = this.time;
                int randomIndex = findIndex();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    recentlySentCommands = commands.get(randomIndex);
                    recentlySentCommands.forEach(c -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c));
                });
            }
        }, 0, 20);
    }

    private int findIndex() {
        int randomIndex = new Random().nextInt(commands.size());
        if (commands.size() != 1 && commands.get(randomIndex).equals(recentlySentCommands))
            return findIndex();
        else return randomIndex;
    }

    public void quit() {
        if (task != null) task.cancel();
    }
}