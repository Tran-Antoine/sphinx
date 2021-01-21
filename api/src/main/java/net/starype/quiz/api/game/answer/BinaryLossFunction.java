package net.starype.quiz.api.game.answer;

public class BinaryLossFunction implements LossFunction {
    private double threshold;

    public BinaryLossFunction(double threshold) {
        this.threshold = threshold;
    }

    public BinaryLossFunction() {
        this(0.001);
    }

    @Override
    public double evaluate(double x) {
        return (x <= threshold) ? (1.0) : (0.0);
    }

}
