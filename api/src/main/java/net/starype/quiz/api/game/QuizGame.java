package net.starype.quiz.api.game;


public interface QuizGame {

    void start();
    void pause();
    void resume();
    void forceStop();

    boolean isCurrentRoundFinished();
    void nextRound();

    void sendInput(PlayerUuidHolder player, String message);
}
