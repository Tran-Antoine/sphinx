package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.ScoreDistribution;

/**
 * A loss function that considers the value 100% correct if in a given range, 0% otherwise.
 */
public class BinaryLossFunction implements LossFunction {

    private double threshold;

    /**
     * Construct a BinaryLossFunction object from the specified threshold (range).
     * @param threshold the 'error' tolerated to consider a 100% accuracy
     */
    public BinaryLossFunction(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Construct a BinaryLossFunction object with {@link ScoreDistribution#EPSILON} threshold.
     */
    public BinaryLossFunction() {
        this(ScoreDistribution.EPSILON);
    }

    @Override
    public double evaluate(double error) {
        return (error <= threshold) ? (1.0) : (0.0);
    }

}
