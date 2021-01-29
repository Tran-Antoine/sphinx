package net.starype.quiz.api.game.answer;

public interface LossFunction {

    double evaluate(double error);

    default double evaluate(double error, double from, double to) {
        return evaluate((error - from) / (to - from));
    }
}
