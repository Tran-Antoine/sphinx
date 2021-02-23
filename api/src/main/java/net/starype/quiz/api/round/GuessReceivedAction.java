package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiConsumer;

public interface GuessReceivedAction extends BiConsumer<RoundState, MutableGuessContext> {
    default GuessReceivedAction followedBy(GuessReceivedAction after) {
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
