package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

public interface GameRound {

    void init();
    void onGuessReceived(UUIDHolder source, String message);
    void onGiveUpReceived(UUIDHolder source);

    AnswerEligibility playerEligibility();
    RoundEndingPredicate endingCondition();
    ScoreDistribution createScoreDistribution();
    GameRoundReport createReport();

    default GameRoundContext createContext() {
        return new GameRoundContext(this);
    }
}
