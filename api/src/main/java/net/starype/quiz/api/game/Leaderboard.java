package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Leaderboard {
    private List<Standing> standings = new ArrayList<>();

    public void insertNewPlayer(Standing playerPosition) {
        int index = 0;
        for(Standing seat : standings) {
            if(playerPosition.getScoreAcquired() > (seat.getScoreAcquired() + 0.01)) {
                standings.add(index, playerPosition);
                return;
            }
            index++;
        }
        standings.add(playerPosition);
    }

    public boolean contains(Player<?> player) {
        return standings.stream().anyMatch(standing -> standing.getPlayer().equals(player));
    }

    public void set(Player<?> player, double newScore) {
        standings.removeIf(standing -> standing.getPlayer().equals(player));
        insertNewPlayer(new Standing(player, newScore));
    }

    public List<? extends Standing> getStandings() {
        return standings;
    }

    public Optional<Double> getByPlayer(Player<?> player) {
        Double score = null;
        for(Standing standing : standings) {
            if(standing.getPlayer().equals(player)) {
                score = standing.getScoreAcquired();
            }
        }
        return Optional.ofNullable(score);
    }

    public int getPosition(Player<?> player) {
        int position = 0;
        for(int i = 0; i < standings.size(); i++) {
            if(standings.get(i).getPlayer().equals(player)) {
                position = i;
            }
        }
        return position;
    }
}