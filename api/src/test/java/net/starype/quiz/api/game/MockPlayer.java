package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.UUID;

class MockPlayer extends Player {

    public MockPlayer() {
        super(UUID.randomUUID(), "");
    }
}
