package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

public class BinaryDistribution implements ScoreDistribution {
    private double threshold;
    private Leaderboard leaderboard;
    private double scoreForWinner;

    public BinaryDistribution(double threshold, Leaderboard leaderboard,
                              double scoreForWinner) {
        this.threshold = threshold;
        this.leaderboard = leaderboard;
        this.scoreForWinner = scoreForWinner;
    }

    public BinaryDistribution(Leaderboard leaderboard, double scoreForWinner) {
        this(0.5, leaderboard, scoreForWinner);
    }

    @Override
    public Double apply(Player<?> player) {
        double correctness = leaderboard.getByPlayer(player).orElse(0.0);
        return correctness < threshold
                ? 0.0
                : scoreForWinner;
    }
}
