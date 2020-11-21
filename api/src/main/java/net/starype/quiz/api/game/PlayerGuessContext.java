package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

public class PlayerGuessContext {

    private Player<?> player;
    private double correctness;
    private boolean eligibility;

    public PlayerGuessContext(Player<?> player, double correctness, boolean eligibility) {
        this.player = player;
        this.correctness = correctness;
        this.eligibility = eligibility;
    }

    public double getCorrectness() {
        return correctness;
    }

    public Player<?> getPlayer() {
        return player;
    }

    public boolean isEligible() {
        return eligibility;
    }
}
