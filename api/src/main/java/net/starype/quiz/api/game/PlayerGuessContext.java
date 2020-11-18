package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

public class PlayerGuessContext {

    private IDHolder player;
    private double correctness;
    private boolean eligibility;

    public PlayerGuessContext(IDHolder player, double correctness, boolean eligibility) {
        this.player = player;
        this.correctness = correctness;
        this.eligibility = eligibility;
    }

    public double getCorrectness() {
        return correctness;
    }

    public IDHolder getPlayer() {
        return player;
    }

    public boolean isEligible() {
        return eligibility;
    }
}
