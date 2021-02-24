package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.player.Player;

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
        return new ArrayList<>(standings);
    }

    public Optional<Double> getByPlayer(Player<?> player) {
        return standings.stream()
                .filter(standing -> standing.getPlayer().equals(player))
                .findAny()
                .map(Standing::getScoreAcquired);
    }

    public Optional<Integer> getPosition(Player<?> player) {
        return standings.stream()
                .filter(standing -> standing.getPlayer().equals(player))
                .findAny()
                .map(standing -> standings.indexOf(standing));
    }
}
