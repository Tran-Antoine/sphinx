package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * Add the current player's correctness if there is no correctness linked to this player in the leaderboard
 */
public class AddCorrectnessIfNew extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        roundState.addCorrectnessIfNew(playerGuessContext.getPlayer(), playerGuessContext.getCorrectness());
    }
}
