package net.starype.quiz.api.game;

import net.starype.quiz.api.player.IDHolder;

public class MaxGuess implements EntityEligibility {
    private GuessCounter counter;

    public MaxGuess(GuessCounter counter) {
        this.counter = counter;
    }

    @Override
    public boolean isEligible(IDHolder<?> player) {
        return counter.maxReached(player);
    }
}
