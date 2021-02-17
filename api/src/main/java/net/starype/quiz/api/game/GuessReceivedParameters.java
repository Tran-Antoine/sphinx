package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public class GuessReceivedParameters {

    private String message;
    private Double correctness;
    private RoundState roundState;
    private PlayerGuessContext playerGuessContext;

    public GuessReceivedParameters(String message, Double correctness, RoundState roundState,
                                   PlayerGuessContext playerGuessContext) {
        this.message = message;
        this.correctness = correctness;
        this.roundState = roundState;
        this.playerGuessContext = playerGuessContext;
    }


    public String getMessage() {
        return message;
    }

    public Double getCorrectness() {
        return correctness;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public PlayerGuessContext getPlayerGuessContext() {
        return playerGuessContext;
    }
}
