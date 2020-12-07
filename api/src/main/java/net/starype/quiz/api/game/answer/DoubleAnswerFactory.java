package net.starype.quiz.api.game.answer;

import java.util.Set;

public class DoubleAnswerFactory implements RangedAnswerFactory {
    private double range = 1.0;
    private LossFunction lossFunction = new LinearLossFunction();

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return DoubleValidity.getInstance();
    }

    @Override
    public AnswerEvaluator createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new DoubleAnswerEvaluator(new NumberCorrectness(processList(answers, answerProcessor), range, lossFunction));
    }

    @Override
    public RangedAnswerFactory withAcceptedRange(Number range) {
        this.range = Math.abs(range.doubleValue());
        return this;
    }

    @Override
    public RangedAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }
}
