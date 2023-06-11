package it.fulminazzo.sbcAPI;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

public class StrippedBroadcastEventB extends Event implements Cancellable {
    private List<ProxiedPlayer> players;
    private String message;
    private boolean cancelled;

    public StrippedBroadcastEventB(List<ProxiedPlayer> players, String message) {
        this.message = message;
        this.players = players;
        this.cancelled = false;
    }

    public List<ProxiedPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ProxiedPlayer> players) {
        this.players = players;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}