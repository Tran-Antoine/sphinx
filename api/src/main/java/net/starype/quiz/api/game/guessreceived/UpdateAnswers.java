package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

public class UpdateAnswers extends GuessReceivedHead {

    @Override
    public void accept(Player<?> player, String message, Double correctness,
                       RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        roundState.updateRoundAnswers(player, Answer.fromString(message));
    }
}
