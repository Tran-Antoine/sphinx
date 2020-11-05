package net.starype.quiz.api.game;

import java.util.UUID;

public interface QuizGame {

    void start();
    void pause();
    void resume();
    void forceStop();

    boolean isCurrentRoundFinished();
    void nextRound();

    void sendInput(UUID id, String message);
}
