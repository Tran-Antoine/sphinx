package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.GuessReceivedParameters;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

/**
 * GuessReceivedHead which adds the player's answer to answers if there is no answer linked to the
 * current player in answers. Else replace te old answer.
 */
public class UpdateAnswers extends GuessReceivedHead {

    @Override
    public void accept(GuessReceivedParameters parameters) {
        Player<?> player = parameters.getPlayerGuessContext().getPlayer();
        String message = parameters.getMessage();
        parameters.getRoundState().updateRoundAnswers(player, Answer.fromString(message));
    }
}
