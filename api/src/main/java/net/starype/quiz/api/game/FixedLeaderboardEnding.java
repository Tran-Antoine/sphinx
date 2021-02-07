package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.Map;

public class FixedLeaderboardEnding implements RoundEndingPredicate, PlayersSettable {

    private Map<? extends Player<?>, Double> leaderboard;
    private int playersCount;

    public FixedLeaderboardEnding(LeaderboardDistribution leaderboardDistribution) {
        this.leaderboard = leaderboardDistribution.getLeaderboard();
    }

    @Override
    public boolean ends() {
        return fullAndOneNonGraded() || fullAndOneBelow();
    }

    private boolean fullAndOneNonGraded() {
        if(playersCount - leaderboard.size() != 1) {
            return false;
        }
        return leaderboard.values()
                .stream()
                .allMatch((aDouble -> Math.abs(aDouble) < 0.001));
    }

    private boolean fullAndOneBelow() {
        if(playersCount != leaderboard.size()) {
            return false;
        }
        return leaderboard.values()
                .stream()
                .filter(aDouble -> Math.abs(aDouble - 1) > 0.001)
                .count() == 1;
    }

    @Override
    public void setPlayers(Collection<? extends Player<?>> players) {
        this.playersCount = players.size();
    }
}
