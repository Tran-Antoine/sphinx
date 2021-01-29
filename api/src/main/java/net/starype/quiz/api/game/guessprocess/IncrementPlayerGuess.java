package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.player.Player;

public class IncrementPlayerGuess extends ConditionalConsumer<RoundState> {

    @Override
    public void execute(RoundState roundState) {
        MaxGuessCounter counter = roundState.getCounter();
        Player<?> player = roundState.getPlayerGuessContext().getPlayer();
        counter.incrementGuess(player);
    }
}
