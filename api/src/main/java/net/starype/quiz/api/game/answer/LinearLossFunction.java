package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.game.utils.MathUtils;

public class LinearLossFunction implements LossFunction
{
    @Override
    public double evaluate(double x) {
        return MathUtils.clamp01(1 - x);
    }
}
