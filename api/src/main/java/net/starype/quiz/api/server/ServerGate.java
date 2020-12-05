package net.starype.quiz.api.server;

import net.starype.quiz.api.game.QuizGame;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerGate<T extends QuizGame> {

    private GameServer<? super T> server;
    private T game;

    public ServerGate(GameServer<? super T> server) {
        this(server, null);
    }

    public ServerGate(GameServer<? super T> server, T game) {
        this.server = server;
        this.game = game;
    }

    public void gameCallback(BiConsumer<GameServer<? super T>, T> request) {
        Objects.requireNonNull(game, "Can't perform game call backs with no game set");
        request.accept(server, game);
    }

    public void callback(Consumer<GameServer<?>> request) {
        request.accept(server);
    }

    public ServerGate<T> withGame(T game) {
        return new ServerGate<>(server, game);
    }
}
