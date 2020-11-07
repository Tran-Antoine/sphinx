package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;

public interface EntityEligibility {

    boolean isEligible(UUIDHolder player);
    default boolean existsEligible(Collection<? extends UUIDHolder> players) {
        return players.stream().anyMatch(this::isEligible);
    }

    default EntityEligibility or(EntityEligibility other) {
        return (player) -> this.isEligible(player) || other.isEligible(player);
    }

    default EntityEligibility and(EntityEligibility other) {
        return (player) -> this.isEligible(player) && other.isEligible(player);
    }
}
