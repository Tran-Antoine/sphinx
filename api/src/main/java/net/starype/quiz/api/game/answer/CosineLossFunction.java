package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.utils.MathUtils;

public class CosineLossFunction implements LossFunction {

    @Override
    public double evaluate(double x) {
        return MathUtils.Clamp01(Math.cos((Math.PI / 2) * x));
    }
}
