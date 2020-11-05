package net.starype.quiz.api.game;


import net.starype.quiz.api.game.player.UUIDHolder;

public interface QuizGame {

    void start();
    void pause();
    void resume();
    void forceStop();

    boolean isCurrentRoundFinished();
    void nextRound();

    void sendInput(UUIDHolder player, String message);
}
