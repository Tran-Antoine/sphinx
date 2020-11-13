package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.HashMap;
import java.util.Map;

public class MaxGuessCounter implements EntityEligibility {

    private int maxGuesses;
    private Map<UUIDHolder, Integer> guessesPerPlayer;

    public MaxGuessCounter(int maxGuesses) {
        this.maxGuesses = maxGuesses;
        this.guessesPerPlayer = new HashMap<>();
    }

    public void incrementGuess(UUIDHolder holder) {
        guessesPerPlayer.put(holder, guessesPerPlayer.getOrDefault(holder, 0) + 1);
    }

    public void consumeAllGuesses(UUIDHolder holder) {
        guessesPerPlayer.put(holder, maxGuesses);
    }

    @Override
    public boolean isEligible(UUIDHolder player) {
        return guessesPerPlayer.getOrDefault(player, 0) < maxGuesses;
    }
}
