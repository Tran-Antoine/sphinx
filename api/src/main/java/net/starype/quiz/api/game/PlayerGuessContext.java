package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

public class PlayerGuessContext {

    private UUIDHolder player;
    private double correctness;
    private boolean eligibility;

    public PlayerGuessContext(UUIDHolder player, double correctness, boolean eligibility) {
        this.player = player;
        this.correctness = correctness;
        this.eligibility = eligibility;
    }

    public double getCorrectness() {
        return correctness;
    }

    public UUIDHolder getPlayer() {
        return player;
    }

    public boolean isEligible() {
        return eligibility;
    }
}
