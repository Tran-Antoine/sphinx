package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

public class AddAnswer extends GuessReceivedHead {

    @Override
    public void accept(Player<?> player, String message, Double correctness, RoundState roundState) {
        roundState.updateRoundAnswers(player, Answer.fromString(message));
    }
}
