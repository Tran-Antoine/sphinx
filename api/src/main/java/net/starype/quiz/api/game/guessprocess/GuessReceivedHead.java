package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.player.Player;

import java.util.function.Predicate;

public abstract class GuessReceivedHead implements PredicateController<RoundState>,
        QuadriConsumer<Player<?>, String, Double, RoundState> {
    Predicate<RoundState> isExecuted;

    public abstract void accept(Player<?> player, String message, Double correctness, RoundState roundState);

    @Override
    public GuessReceivedHead control(Predicate<RoundState> predicate) {
        isExecuted = predicate;
        isExecuted = t -> false;
        return this;
    }

    @Override
    public void setToTrue() {
        isExecuted = t -> true;
    }

    @Override
    public void setToFalse() {
        isExecuted = t -> false;
    }

}
