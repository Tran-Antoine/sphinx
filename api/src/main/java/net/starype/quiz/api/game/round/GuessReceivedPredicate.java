package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

public interface GuessReceivedPredicate extends BiPredicate<RoundState, MutableGuessContext> {
}
