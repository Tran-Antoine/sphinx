package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

public class ZeroScoreDistribution implements ScoreDistribution {

    @Override
    public Double apply(Player player) {
        return 0.0;
    }
}
