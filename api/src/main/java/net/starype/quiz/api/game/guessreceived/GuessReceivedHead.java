package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

import java.util.function.BiPredicate;

public abstract class GuessReceivedHead implements
        PentaConsumer<Player<?>, String, Double, RoundState, SettablePlayerGuessContext> {

    BiPredicate<RoundState, SettablePlayerGuessContext> isExecuted;

    public abstract void accept(Player<?> player, String message, Double correctness,
                                RoundState roundState, SettablePlayerGuessContext playerGuessContext);

}
