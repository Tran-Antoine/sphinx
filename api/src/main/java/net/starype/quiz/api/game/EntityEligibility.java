package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public interface EntityEligibility {

    boolean isEligible(IDHolder<?> player);
    default boolean existsEligible(Collection<? extends IDHolder<?>> players) {
        return players.stream().anyMatch(this::isEligible);
    }

    default EntityEligibility or(EntityEligibility other) {
        return (player) -> this.isEligible(player) || other.isEligible(player);
    }

    default EntityEligibility and(EntityEligibility other) {
        return (player) -> this.isEligible(player) && other.isEligible(player);
    }
}
