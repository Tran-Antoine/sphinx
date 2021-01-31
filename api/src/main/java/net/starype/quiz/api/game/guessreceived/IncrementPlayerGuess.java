package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

public class IncrementPlayerGuess extends ConditionalBiConsumer<RoundState, SettablePlayerGuessContext> {

    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        Player<?> player = playerGuessContext.getPlayer();
        counter.incrementGuess(player);
    }
}
