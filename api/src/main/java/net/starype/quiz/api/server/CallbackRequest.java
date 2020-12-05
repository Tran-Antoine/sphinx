package net.starype.quiz.api.server;

import java.util.function.Consumer;

public class CallbackRequest {

    private GameServer<?> server;

    public CallbackRequest(GameServer<?> server) {
        this.server = server;
    }

    public void callback(Consumer<GameServer<?>> request) {
        request.accept(server);
    }
}
