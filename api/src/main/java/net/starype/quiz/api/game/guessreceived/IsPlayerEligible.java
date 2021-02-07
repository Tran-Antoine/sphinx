package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

/**
 * BiPredicate that tests if the current guessing player is eligible or not
 */
public class IsPlayerEligible extends ConditionalConsumer {

    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        if(roundState.isPlayerEligible(playerGuessContext.getPlayer())) {
            setControlledBoolean(true);
        } else {
            setControlledBoolean(false);
        }
    }
}
