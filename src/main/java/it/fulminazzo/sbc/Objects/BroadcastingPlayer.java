package it.fulminazzo.sbc.Objects;

import it.fulminazzo.sbc.Enums.MessageType;

import java.util.List;
import java.util.UUID;

public class BroadcastingPlayer {
    private final UUID uuid;
    private final List<UUID> receivers;
    private final MessageType messageType;
    private final boolean rainbow;

    public BroadcastingPlayer(UUID uuid, List<UUID> receivers, MessageType messageType, boolean rainbow) {
        this.uuid = uuid;
        this.receivers = receivers;
        this.messageType = messageType;
        this.rainbow = rainbow;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getReceivers() {
        return receivers;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean isRainbow() {
        return rainbow;
    }
}