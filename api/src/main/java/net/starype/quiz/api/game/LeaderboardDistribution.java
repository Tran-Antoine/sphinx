package net.starype.quiz.api.game;

import net.starype.quiz.api.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class LeaderboardDistribution implements ScoreDistribution {

    private double maxAwarded;
    private Leaderboard leaderboard;
    private int playersCount = 0;

    public LeaderboardDistribution(double maxAwarded, Leaderboard leaderboard) {
        this.maxAwarded = maxAwarded;
        this.leaderboard = leaderboard;
    }

    @Override
    public List<Standing> applyAll(Collection<? extends Player<?>> players,
                                   BiConsumer<Player<?>, Double> action) {
        this.playersCount = players.size();
        return ScoreDistribution.super.applyAll(players, action);
    }

    @Override
    public Double apply(Player<?> player) {
        double score = leaderboard.getByPlayer(player).orElse(0.0);
        if(score == 0.0) {
            return score;
        }

        int position = leaderboard.getPosition(player).get();

        if(playersCount == 1) {
            return maxAwarded;
        }

        double gapPerSeat = maxAwarded/(playersCount - 1);

        return maxAwarded - (gapPerSeat * position);
    }

}
