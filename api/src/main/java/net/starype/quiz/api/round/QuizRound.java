package net.starype.quiz.api.round;

import net.starype.quiz.api.event.UpdatableHandler;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.player.Player;

import java.util.Collection;

public interface QuizRound extends GameRound {

    void start(QuizGame game, Collection<? extends Player<?>> players, UpdatableHandler eventHandler);

    void onGuessReceived(Player<?> source, String message);
    void onGiveUpReceived(Player<?> source);
    default void onRoundStopped(){}
    void checkEndOfRound();
}
