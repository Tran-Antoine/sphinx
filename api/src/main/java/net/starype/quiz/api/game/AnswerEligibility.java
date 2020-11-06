package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;

public interface AnswerEligibility {

    boolean isEligible(UUIDHolder player);
    default boolean existsEligible(Collection<? extends UUIDHolder> players) {
        return players.stream().anyMatch(this::isEligible);
    }

    default AnswerEligibility or(AnswerEligibility other) {
        return (player) -> this.isEligible(player) || other.isEligible(player);
    }

    default AnswerEligibility and(AnswerEligibility other) {
        return (player) -> this.isEligible(player) && other.isEligible(player);
    }
}
