package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

import java.util.HashMap;
import java.util.Map;

public class MaxGuessCounter implements EntityEligibility {

    private int maxGuesses;
    private Map<IDHolder<?>, Integer> guessesPerPlayer;

    public MaxGuessCounter(int maxGuesses) {
        this.maxGuesses = maxGuesses;
        this.guessesPerPlayer = new HashMap<>();
    }

    public void incrementGuess(IDHolder<?> holder) {
        guessesPerPlayer.put(holder, guessesPerPlayer.getOrDefault(holder, 0) + 1);
    }

    public void consumeAllGuesses(IDHolder<?> holder) {
        guessesPerPlayer.put(holder, maxGuesses);
    }

    @Override
    public boolean isEligible(IDHolder<?> player) {
        return guessesPerPlayer.getOrDefault(player, 0) < maxGuesses;
    }
}
