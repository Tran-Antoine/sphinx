package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.player.Player;

import java.util.function.Predicate;

public abstract class HeadConsumer implements PredicateController<RoundState> {
    Predicate<RoundState> controlledCondition;

    public abstract void accept(Player<?> player, String message, Double correctness, RoundState roundState);

    @Override
    public void control(Predicate<RoundState> predicate) {
        controlledCondition = predicate;
    }

    @Override
    public void setToTrue() {
        controlledCondition = t -> true;
    }

    @Override
    public void setToFalse() {
        controlledCondition = t -> false;
    }
}
