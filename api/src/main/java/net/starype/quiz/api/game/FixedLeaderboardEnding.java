package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.RoundState;

public class FixedLeaderboardEnding implements RoundEndingPredicate {

    private Leaderboard leaderboard;
    private int playersCount;

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
    public void initRoundState(RoundState roundState) {
        this.leaderboard = roundState.getLeaderboard();
        this.playersCount = roundState.getPlayers().size();
    }
}
