package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

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
        if(!leaderboard.contains(player)) {
            return 0.0;
        }

        int position = leaderboard.getPosition(player);

        if(playersCount == 1) {
            return maxAwarded;
        }

        double gapPerSeat = maxAwarded/(playersCount - 1);

        return maxAwarded - (gapPerSeat * position);
    }

}
