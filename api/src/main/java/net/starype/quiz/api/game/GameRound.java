package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

public interface GameRound {

    void init();
    void onInputReceived(UUIDHolder source, String message);

    AnswerEligibility playerEligibility();
    RoundEndingPredicate endingCondition();
    ScoreDistribution createScoreDistribution();
    GameRoundReport createReport();

    default GameRoundContext createContext() {
        return new GameRoundContext(this);
    }
}
