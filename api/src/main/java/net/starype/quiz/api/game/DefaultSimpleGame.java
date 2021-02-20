package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;
import net.starype.quiz.api.server.ServerGate;

import java.util.Collection;
import java.util.Queue;

public class DefaultSimpleGame extends SimpleGame<SimpleGame<?>> {

    public DefaultSimpleGame(
            Queue<? extends QuizRound> rounds,
            Collection<? extends Player<?>> players,
            GameServer<? super SimpleGame<?>> server) {

        super(rounds, players);
        setGate(new ServerGate<>(server, this));
    }
}
