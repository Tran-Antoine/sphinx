package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

public interface PlayerGuessContext {
    double getCorrectness();
    boolean isEligible();
    Player<?> getPlayer();
    boolean isAnswerValid();
    Answer getAnswer();
}
