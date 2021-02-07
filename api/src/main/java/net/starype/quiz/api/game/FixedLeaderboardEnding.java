package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.Map;

public class FixedLeaderboardEnding implements RoundEndingPredicate, PlayersSettable {

    private Leaderboard leaderboard;
    private int playersCount;

    public FixedLeaderboardEnding(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public boolean ends() {
        return fullAndOneNonGraded() || fullAndOneBelow();
    }

    private boolean fullAndOneNonGraded() {
        if(playersCount - leaderboard.getStandings().size() != 1) {
            return false;
        }
        return leaderboard.getStandings()
                .stream()
                .allMatch((seat) -> Math.abs(seat.getScoreAcquired() - 1) < ScoreDistribution.EPSILON);
    }

    private boolean fullAndOneBelow() {
        if(playersCount != leaderboard.getStandings().size()) {
            return false;
        }
        return leaderboard.getStandings()
                .stream()
                .filter((seat) -> Math.abs(seat.getScoreAcquired() - 1) > ScoreDistribution.EPSILON)
                .count() == 1;
    }

    @Override
    public void setPlayers(Collection<? extends Player<?>> players) {
        this.playersCount = players.size();
    }
}
