package net.starype.quiz.api.game.player;

import java.util.UUID;

public class PlayerUuidHolder {
    private UUID uuid;

    public PlayerUuidHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}