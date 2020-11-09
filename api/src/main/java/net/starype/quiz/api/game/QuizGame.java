package net.starype.quiz.api.game;


import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.server.GameServer;

import java.util.function.Consumer;

public interface QuizGame {

    void start();
    void pause();
    void resume();
    void forceStop();

    boolean isCurrentRoundFinished();
    void nextRound();

    void onInputReceived(UUIDHolder player, String message);
    void checkEndOfRound(GameRoundContext context);
    void sendInputToServer(Consumer<GameServer> action);
}
