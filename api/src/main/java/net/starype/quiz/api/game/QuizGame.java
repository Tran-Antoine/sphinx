package net.starype.quiz.api.game;


import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.server.GameServer;

import java.util.function.Consumer;

public interface QuizGame {
    void start();
    void pause();
    void resume();
    void update();
    void forceStop();

    boolean isCurrentRoundFinished();
    void nextRound();

    void onInputReceived(IDHolder<?> player, String message);
    void checkEndOfRound(GameRound round);
    void sendInputToServer(Consumer<GameServer> action);
}
