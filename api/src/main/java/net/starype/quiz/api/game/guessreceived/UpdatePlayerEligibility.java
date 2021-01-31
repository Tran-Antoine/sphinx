package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class UpdatePlayerEligibility extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(roundState.isPlayerEligible(playerGuessContext.getPlayer()));
    }
}
