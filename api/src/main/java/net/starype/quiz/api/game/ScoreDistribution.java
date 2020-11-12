package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ScoreDistribution extends Function<Player, Double> {

    default void applyAll(Collection<? extends Player> players, BiConsumer<Player, Double> action) {
        for(Player player : players) {
            action.accept(player, this.apply(player));
        }
    }
}
