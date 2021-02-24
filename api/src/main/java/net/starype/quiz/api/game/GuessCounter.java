package net.starype.quiz.api.game;

import net.starype.quiz.api.player.IDHolder;

import java.util.HashMap;
import java.util.Map;

public class GuessCounter {

    private int maxGuesses;
    private Map<IDHolder<?>, Integer> guessesPerPlayer;

    public GuessCounter(int maxGuesses) {
        this.maxGuesses = maxGuesses;
        this.guessesPerPlayer = new HashMap<>();
    }

    public void incrementGuess(IDHolder<?> holder) {
        guessesPerPlayer.put(holder, guessesPerPlayer.getOrDefault(holder, 0) + 1);
    }

    public void consumeAllGuesses(IDHolder<?> holder) {
        guessesPerPlayer.put(holder, maxGuesses);
    }

    public int getPlayerGuess(IDHolder<?> holder) {
        return guessesPerPlayer.getOrDefault(holder, 0);
    }

    public boolean maxReached(IDHolder<?> holder) {
        return getPlayerGuess(holder) < maxGuesses;
    }
}
