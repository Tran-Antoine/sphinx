package net.starype.quiz.api.game.mock;

import net.starype.quiz.api.game.player.IDHolder;

import java.util.UUID;

public class MockUUIDHolder implements IDHolder {
    private UUID id = UUID.randomUUID();

    @Override
    public UUID getId() {
        return id;
    }
}
