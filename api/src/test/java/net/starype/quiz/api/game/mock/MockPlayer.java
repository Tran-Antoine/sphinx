package net.starype.quiz.api.game.mock;

import net.starype.quiz.api.game.player.Player;

import java.util.UUID;

public class MockPlayer extends Player<UUID> {

    public MockPlayer() {
        super(UUID.randomUUID(), "");
    }
}
