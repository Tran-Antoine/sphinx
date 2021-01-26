package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.ScoreDistribution;

public class BinaryLossFunction implements LossFunction {
    private double threshold = .5;

    public BinaryLossFunction(double threshold) {
        this.threshold = threshold;
    }

    public BinaryLossFunction() {
        this(ScoreDistribution.EPSILON);
    }

    @Override
    public double evaluate(double x) {
        return (x <= threshold) ? (1.0) : (0.0);
    }

}
