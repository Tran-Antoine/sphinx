package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public interface GameRound {

    void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler);
    PlayerGuessContext onGuessReceived(IDHolder<?> source, String message);
    void onGiveUpReceived(IDHolder<?> source);
    default void onRoundStopped(){}

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport();

    GameRoundContext getContext();
}
