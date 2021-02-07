package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

import java.util.function.BiConsumer;

public interface GuessReceivedAction extends BiConsumer<RoundState, SettablePlayerGuessContext> {
    default GuessReceivedAction followedBy(GuessReceivedAction after) {
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
