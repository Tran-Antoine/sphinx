package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

public interface GameRound {

    void start();
    void onInputReceived(UUIDHolder source, String message);

    RoundEndingPredicate endingCondition();
    ScoreDistribution createScoreDistribution();
    GameRoundReport createReport();
}
