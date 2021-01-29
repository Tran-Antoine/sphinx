package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

public class ModifiablePlayerReport implements PlayerReport {
    private IDHolder<?> player;
    private String answeredSolution;
    private int numberOfTrial;
    private double reward;
    private boolean hGivenUp;

    public ModifiablePlayerReport(IDHolder<?> player) {
        this(player, "", 0, 0.0, false);
    }

    public ModifiablePlayerReport(IDHolder<?> player, String answeredSolution, int numberOfTrial, double reward, boolean hasGivenUp) {
        this.player = player;
        this.answeredSolution = answeredSolution;
        this.numberOfTrial = numberOfTrial;
        this.reward = reward;
        this.hGivenUp = hasGivenUp;
    }

    @Override
    public double getReward() {
        return reward;
    }

    @Override
    public IDHolder<?> getPlayer() {
        return player;
    }

    @Override
    public int getNumberOfTrial() {
        return numberOfTrial;
    }

    @Override
    public boolean hasGivenUp() {
        return hGivenUp;
    }

    @Override
    public String getAnsweredSolution() {
        return answeredSolution;
    }

    public void registerSolution(String answeredSolution) {
        this.answeredSolution = answeredSolution;
        incrementTrials();
    }

    public void giveUp() {
        hGivenUp = true;
    }

    public void incrementTrials() {
        this.numberOfTrial++;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}
