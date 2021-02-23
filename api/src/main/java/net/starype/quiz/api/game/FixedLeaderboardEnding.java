package net.starype.quiz.api.game;

import net.starype.quiz.api.round.RoundState;

public class FixedLeaderboardEnding extends RoundEndingPredicate {

    private Leaderboard leaderboard;
    private int playersCount;

    public FixedLeaderboardEnding(RoundState roundState) {
        super(roundState);
    }

    @Override
    public boolean ends() {
        this.leaderboard = getRoundState().getLeaderboard();
        this.playersCount = getRoundState().getPlayers().size();
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

}
