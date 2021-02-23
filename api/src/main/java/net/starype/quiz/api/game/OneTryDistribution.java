package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

public class OneTryDistribution implements ScoreDistribution {

    private double maxToAward;
    private Leaderboard leaderboard;

    public OneTryDistribution(double maxToAward, Leaderboard leaderboard) {
        this.maxToAward = maxToAward;
        this.leaderboard = leaderboard;
    }

    @Override
    public Double apply(Player player) {
        return maxToAward * leaderboard.getByPlayer(player).orElse(0.0);
    }
}
