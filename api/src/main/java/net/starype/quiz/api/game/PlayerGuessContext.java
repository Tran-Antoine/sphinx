package net.starype.quiz.api.game;

import net.starype.quiz.api.answer.Answer;
import net.starype.quiz.api.player.Player;

public interface PlayerGuessContext {
    double getCorrectness();
    boolean isEligible();
    Player<?> getPlayer();
    boolean isAnswerValid();
    Answer getAnswer();
}
