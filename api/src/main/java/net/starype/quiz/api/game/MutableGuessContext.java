package net.starype.quiz.api.game;

import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.Player;

public class MutableGuessContext implements PlayerGuessContext {

    private Player<?> player;
    private double correctness;
    private boolean eligibility;
    private Answer answer;
    private boolean isAnswerValid;

    public MutableGuessContext(Player<?> player, double correctness, boolean eligibility, Answer answer,
                               boolean isAnswerValid) {
        this.player = player;
        this.correctness = correctness;
        this.eligibility = eligibility;
        this.answer = answer;
        this.isAnswerValid = isAnswerValid;
    }

    public double getCorrectness() {
        return correctness;
    }

    public Player<?> getPlayer() {
        return player;
    }

    @Override
    public boolean isAnswerValid() {
        return isAnswerValid;
    }

    @Override
    public Answer getAnswer() {
        return answer;
    }

    public boolean isEligible() {
        return eligibility;
    }

    public void setCorrectness(double correctness) {
        this.correctness = correctness;
    }

    public void setEligibility(boolean eligibility) {
        this.eligibility = eligibility;
    }
}
