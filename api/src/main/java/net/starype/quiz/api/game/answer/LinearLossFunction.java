package net.starype.quiz.api.game.answer;

import net.starype.quiz.api.util.MathUtils;

public class LinearLossFunction implements LossFunction
{
    @Override
    public double evaluate(double error) {
        return MathUtils.clamp01(1 - error);
    }
}
