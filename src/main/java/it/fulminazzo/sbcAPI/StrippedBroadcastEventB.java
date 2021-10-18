package it.fulminazzo.sbcAPI;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

public class StrippedBroadcastEventB extends Event {
    private List<ProxiedPlayer> players;
    private String message;

    public StrippedBroadcastEventB(List<ProxiedPlayer> players, String message) {
        this.message = message;
        this.players = players;
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
}