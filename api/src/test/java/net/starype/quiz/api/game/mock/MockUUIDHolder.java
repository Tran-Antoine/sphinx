package net.starype.quiz.api.game.mock;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.UUID;

public class MockUUIDHolder implements UUIDHolder {
    private UUID id = UUID.randomUUID();

    @Override
    public UUID getUUID() {
        return id;
    }
}
