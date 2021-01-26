package net.starype.quiz.api.game;

import net.starype.quiz.api.game.LeaderboardDistribution;
import net.starype.quiz.api.game.LeaderboardDistribution.LeaderboardPosition;
import net.starype.quiz.api.game.RoundEndingPredicate;

import java.util.Collection;

public class FixedLeaderboardEnding implements RoundEndingPredicate {

    private Collection<? extends LeaderboardPosition> leaderboard;
    private int playersCount;

    public FixedLeaderboardEnding(LeaderboardDistribution leaderboardDistribution, int playersCount) {
        this.leaderboard = leaderboardDistribution.getLeaderboard();
        this.playersCount = playersCount;
    }

    @Override
    public boolean ends() {
        return fullAndOneNonGraded() || fullAndOneBelow();
    }

    private boolean fullAndOneNonGraded() {
        if(playersCount - leaderboard.size() != 1) {
            return false;
        }
        return leaderboard
                .stream()
                .allMatch((seat) -> Math.abs(seat.getScore() - 1) < ScoreDistribution.EPSILON);
    }

    private boolean fullAndOneBelow() {
        if(playersCount != leaderboard.size()) {
            return false;
        }
        return leaderboard
                .stream()
                .filter((seat) -> Math.abs(seat.getScore() - 1) > ScoreDistribution.EPSILON)
                .count() == 1;
    }
}
