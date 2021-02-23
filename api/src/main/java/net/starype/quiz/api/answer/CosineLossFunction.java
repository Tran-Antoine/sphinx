package net.starype.quiz.api.answer;

import net.starype.quiz.api.util.MathUtils;

public class CosineLossFunction implements LossFunction {

    @Override
    public double evaluate(double error) {
        return MathUtils.clamp01(Math.cos((Math.PI / 2) * error));
    }
}
