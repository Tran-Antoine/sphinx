package net.starype.quiz.api.game.player;

import java.util.UUID;

public class UUIDHolder {
    private UUID uuid;

    public UUIDHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
