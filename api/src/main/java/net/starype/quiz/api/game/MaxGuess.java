package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

public class MaxGuess implements EntityEligibility {
    GuessCounter counter;

    public MaxGuess(GuessCounter counter) {
        this.counter = counter;
    }

    @Override
    public boolean isEligible(IDHolder<?> player) {
        return counter.getPlayerGuess(player) < counter.getMaxGuesses();
    }
}
