package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

/**
 * GuessReceivedHead which adds the player's answer to answers if there is no answer linked to the
 * current player in answers. Else replace te old answer.
 */
public class UpdateAnswers extends GuessReceivedHead {

    @Override
    public void accept(Player<?> player, String message, Double correctness,
                        RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        roundState.updateRoundAnswers(player, Answer.fromString(message));
    }
}
