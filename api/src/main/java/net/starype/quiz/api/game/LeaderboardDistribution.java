package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class LeaderboardDistribution implements ScoreDistribution {

    private double maxAwarded;
    private int playersCount;
    private List<LeaderboardPosition> leaderboard;

    public LeaderboardDistribution(double maxAwarded, int playersCount) {
        this.maxAwarded = maxAwarded;
        this.playersCount = playersCount;
        this.leaderboard = new ArrayList<>();
    }


    public void scoreUpdate(UUIDHolder player, double score) {
        Optional<LeaderboardPosition> seat = getCurrent(player);
        if(seat.isEmpty()) {
            insertNewPlayer(new LeaderboardPosition(player, score));
            return;
        }
        LeaderboardPosition current = seat.get();
        double currentScore = current.score;
        if(score < currentScore) {
            return;
        }
        leaderboard.remove(current);
        insertNewPlayer(new LeaderboardPosition(player, score));
    }

    private void insertNewPlayer(LeaderboardPosition playerPosition) {
        int index = 0;
        for(LeaderboardPosition seat : leaderboard) {
            if(playerPosition.score > (seat.score + 0.01)) {
                leaderboard.add(index, playerPosition);
                return;
            }
            index++;
        }
        leaderboard.add(playerPosition);
    }

    private Optional<LeaderboardPosition> getCurrent(UUIDHolder player) {
        return leaderboard
                .stream()
                .filter((seat) -> seat.player.equals(player))
                .findAny();
    }

    @Override
    public Double apply(Player player) {
        Optional<LeaderboardPosition> seat = getCurrent(player);
        if(seat.isEmpty()) {
            return 0D;
        }
        if(leaderboard.size() == 1) {
            return maxAwarded;
        }

        int position = leaderboard.indexOf(seat.get());
        double gapPerSeat = maxAwarded / (playersCount - 1);
        return maxAwarded - (position * gapPerSeat);
    }

    public Collection<? extends LeaderboardPosition> getLeaderboard() {
        return leaderboard;
    }

    public static class LeaderboardPosition {

        private final UUIDHolder player;
        private final double score;

        private LeaderboardPosition(UUIDHolder player, double score) {
            this.player = player;
            this.score = score;
        }

        public UUIDHolder getPlayer() {
            return player;
        }

        public double getScore() {
            return score;
        }
    }
}
