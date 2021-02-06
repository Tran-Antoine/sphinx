package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

/**
 * Update the eligibility of the player guess context according to the eligibility of the round state
 */
public class UpdatePlayerEligibility extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(roundState.isPlayerEligible(playerGuessContext.getPlayer()));
    }
}
