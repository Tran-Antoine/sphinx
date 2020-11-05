package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.PlayerUuidHolder;

public interface GameRound {

    void start();
    void onInputReceived(PlayerUuidHolder source, String message);

    RoundEndingPredicate endingCondition();
    ScoreDistribution createScoreDistribution();
    GameRoundReport createReport();
}
