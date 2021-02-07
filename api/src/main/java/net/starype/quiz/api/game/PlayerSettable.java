package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;

public interface PlayerSettable {
    void setPlayers(Collection<? extends Player<?>> players);
}
