package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

public interface GuessReceivedPredicate extends BiPredicate<RoundState, MutableGuessContext> {
    @Override
    default GuessReceivedPredicate negate() {
        return (roundState, guessContext) -> !test(roundState, guessContext);
    }

    @Override
    default GuessReceivedPredicate and(BiPredicate<? super RoundState, ? super MutableGuessContext> other) {
        return (roundState, guessContext) ->
                this.test(roundState, guessContext) && other.test(roundState, guessContext);
    }

    @Override
    default GuessReceivedPredicate or(BiPredicate<? super RoundState, ? super MutableGuessContext> other) {
        return (roundState, guessContext) ->
                this.test(roundState, guessContext) || other.test(roundState, guessContext);
    }
}
