package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * Add the player's correctness to leaderboard if there is no correctness linked to the current player in leaderboard.
 * Else replace te old correctness.
 */
public class UpdateLeaderboard extends SimpleReceivedAction {

    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        roundState.updateLeaderboard(playerGuessContext.getPlayer(),
                playerGuessContext.getCorrectness());
    }
}
