package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.util.SortUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class LeaderboardDistribution implements ScoreDistribution {

    private double maxAwarded;
    private Map<Player<?>, Double> leaderboard;

    public LeaderboardDistribution(double maxAwarded, Map<Player<?>, Double> leaderboard) {
        this.maxAwarded = maxAwarded;
        this.leaderboard = leaderboard;
    }

    @Override
    public Map<Player<?>, Double> applyAll(Collection<? extends Player<?>> players,
                                           BiConsumer<Player<?>, Double> action) {
        Map<Player<?>, Double> scores = new HashMap<>();
        List<SortUtils.Standing> standings = SortUtils.sortByScore(leaderboard);
        double gapPerSeat = maxAwarded / (players.size() - 1);

        int position = standings.size();
        for (SortUtils.Standing standing : standings) {
            scores.put(standing.getPlayer(), maxAwarded - (position * gapPerSeat));
            action.accept(standing.getPlayer(), maxAwarded - (position * gapPerSeat));
            position--;
        }

        return scores;
    }

    @Override
    public Double apply(Player<?> player) {
        return null;
    }

    public Map<? extends Player<?>, Double> getLeaderboard() {
        return leaderboard;
    }

}
