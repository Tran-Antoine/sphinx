package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public class ResetAllGuessCounters extends ConditionalConsumer<RoundState> {
    @Override
    public void execute(RoundState roundState) {
        MaxGuessCounter counter = roundState.getCounter();
        Collection<? extends IDHolder<?>> players = roundState.getLeaderboard().keySet();
        for(IDHolder<?> player : players) {
            counter.consumeAllGuesses(player);
        }
    }
}
