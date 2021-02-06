package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public interface GameRound {

    void start(QuizGame game, Collection<? extends IDHolder<?>> players, EventHandler eventHandler,
               Consumer<GameRound> checkEndOfRound);
    PlayerGuessContext onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(Player<?> source);
    default void onRoundStopped(){}
    void checkEndOfRound();

    EntityEligibility initPlayerEligibility();
    RoundEndingPredicate initEndingCondition();
    ScoreDistribution initScoreDistribution();
    GameRoundReport initReport(Map<Player<?>, Double> standings);

    GameRoundContext getContext();
}
