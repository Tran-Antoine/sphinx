package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public class ConsumeAllPlayersGuess extends ConditionalBiConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        Collection<? extends IDHolder<?>> players = roundState.getLeaderboard().keySet();
        for(IDHolder<?> player : players) {
            counter.consumeAllGuesses(player);
        }
    }
}
