package net.starype.quiz.api.game;


import net.starype.quiz.api.server.GameServer;

import java.util.function.Consumer;

public interface QuizGame {
    void start();
    void pause();
    void resume();
    void update();
    void forceStop();

    boolean isCurrentRoundFinished();
    boolean containsPlayerId(Object id);
    boolean nextRound();

    void onInputReceived(Object playerId, String message);
    void checkEndOfRound(GameRound round);
    void sendInputToServer(Consumer<GameServer<?>> action);
    void removePlayer(Object playerId);
}
