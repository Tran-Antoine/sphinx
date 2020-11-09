package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

public class PlayerGuessContext {

    private UUIDHolder player;
    private double pointsAwarded;
    private boolean eligibility;

    public PlayerGuessContext(UUIDHolder player, double pointsAwarded, boolean eligibility) {
        this.player = player;
        this.pointsAwarded = pointsAwarded;
        this.eligibility = eligibility;
    }

    public double getPointsAwarded() {
        return pointsAwarded;
    }

    public UUIDHolder getPlayer() {
        return player;
    }

    public boolean isEligible() {
        return eligibility;
    }
}
