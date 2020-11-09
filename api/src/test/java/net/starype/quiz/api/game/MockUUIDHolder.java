package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.UUID;

class MockUUIDHolder implements UUIDHolder {
    private UUID id = UUID.randomUUID();

    @Override
    public UUID getUUID() {
        return id;
    }
}
