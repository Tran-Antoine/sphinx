package net.starype.quiz.api.game;

import java.util.UUID;

public interface GameRound {

    void start();
    void onInputReceived(UUID source, String message);

    RoundEndingPredicate endingCondition();
    ScoreDistribution createScoreDistribution();
    GameRoundReport createReport();
}
