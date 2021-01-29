package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class UpdateLeaderboard extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {

    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        roundState.updateLeaderboard(playerGuessContext.getPlayer(),
                playerGuessContext.getCorrectness());
    }
}
