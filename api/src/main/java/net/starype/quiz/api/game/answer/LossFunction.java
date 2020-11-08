package net.starype.quiz.api.game.answer;

public interface LossFunction {

    double evaluate(double x);

    default double evaluate(double x, double from, double to) {
        return evaluate((x - from) / (to - from));
    }
}
