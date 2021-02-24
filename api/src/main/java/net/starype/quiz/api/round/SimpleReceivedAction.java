package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * A BiConsumer that is linked to a BiPredicate. By default,
 * this SimpleReceivedAction will execute only if the bounded BiPredicate is true.
 */
public abstract class SimpleReceivedAction implements GuessReceivedAction {

    /**
     * The bounded predicate of this SimpleReceivedAction. This consumer will execute only if this bounded predicate
     * is true.
     */
    private GuessReceivedPredicate boundedPredicate = (t, u) -> true;

    protected abstract void execute(RoundState t, MutableGuessContext u);

    @Override
    public void accept(RoundState t, MutableGuessContext u) {
        if (boundedPredicate.test(t, u)) {
            execute(t, u);
        }
    }

    public SimpleReceivedAction withCondition(GuessReceivedPredicate predicate) {
        boundedPredicate = predicate;
        return this;
    }
}
