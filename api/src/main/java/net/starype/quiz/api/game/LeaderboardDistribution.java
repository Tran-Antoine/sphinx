package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class LeaderboardDistribution implements ScoreDistribution {

    private double maxAwarded;
    private Leaderboard leaderboard;

    public LeaderboardDistribution(double maxAwarded, Leaderboard leaderboard) {
        this.maxAwarded = maxAwarded;
        this.leaderboard = leaderboard;
    }

    @Override
    public List<Standing> applyAll(Collection<? extends Player<?>> players,
                                           BiConsumer<Player<?>, Double> action) {
        List<Standing> scores = new ArrayList<>();
        double gapPerSeat = maxAwarded / (players.size() - 1);

        int position = leaderboard.getStandings().size();
        for (Standing standing : leaderboard.getStandings()) {
            scores.add(new Standing(standing.getPlayer(), maxAwarded - (position * gapPerSeat)));
            action.accept(standing.getPlayer(), maxAwarded - (position * gapPerSeat));
            position--;
        }

        return scores;
    }

    @Override
    public Double apply(Player<?> player) {
        return null;
    }


}
