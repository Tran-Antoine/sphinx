package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.GameEventHandler;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;

public interface GameRound {

    void start(QuizGame game, Collection<? extends UUIDHolder> players, GameEventHandler eventHandler);
    void onGuessReceived(UUIDHolder source, String message);
    void onGiveUpReceived(UUIDHolder source);
    void onRoundStopped();

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport();

    GameRoundContext getContext();
}
