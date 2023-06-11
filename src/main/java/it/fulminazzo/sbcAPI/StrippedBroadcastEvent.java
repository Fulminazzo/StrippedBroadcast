package it.fulminazzo.sbcAPI;

import it.fulminazzo.sbc.Enums.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class StrippedBroadcastEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private List<Player> players;
    private String message;
    private MessageType messageType;
    private boolean cancelled;

    public StrippedBroadcastEvent(List<Player> players, MessageType messageType, String message) {
        this.message = message;
        this.messageType = messageType;
        this.players = players;
        this.cancelled = false;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}