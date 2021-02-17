package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

/**
 * A BiConsumer that is linked to a BiPredicate, and controls another boolean. By default,
 * this SimpleReceivedAction will execute only if the bounded BiPredicate is true. The SimpleReceivedAction
 * may also control the value of a boolean to enable/disable the execution of other SimpleReceivedAction.
 */
public abstract class SimpleReceivedAction implements GuessReceivedAction {

    /**
     * The bounded predicate of this SimpleReceivedAction. This consumer will execute only if this bounded predicate
     * is true.
     */
    private BiPredicate<RoundState, MutableGuessContext> boundedPredicate = (t, u) -> true;

    protected abstract void execute(RoundState t, MutableGuessContext u);

    @Override
    public void accept(RoundState t, MutableGuessContext u) {
        if (boundedPredicate.test(t, u)) {
            execute(t, u);
        }
    }

    public SimpleReceivedAction withCondition(BiPredicate<RoundState,
            MutableGuessContext> biPredicate) {
        boundedPredicate = biPredicate;
        return this;
    }
}
