package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

public interface PlayerReport {
    IDHolder<?> getPlayer();
    double getReward();
    int getNumberOfTrial();
    String getAnsweredSolution();
    boolean hasGivenUp();
}
