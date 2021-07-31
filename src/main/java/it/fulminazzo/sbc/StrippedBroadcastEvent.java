package it.fulminazzo.sbc;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class StrippedBroadcastEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private List<Player> players;
    private String message;

    public StrippedBroadcastEvent(List<Player> players, String message) {
        this.message = message;
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}