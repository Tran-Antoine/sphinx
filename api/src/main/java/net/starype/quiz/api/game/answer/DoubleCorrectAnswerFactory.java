package net.starype.quiz.api.game.answer;

import java.util.Set;

public class DoubleCorrectAnswerFactory implements CorrectRangedAnswerFactory {
    private double range = 1.0;
    private LossFunction lossFunction = new LinearLossFunction();

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return DoubleValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new DoubleCorrectAnswer(new DoubleCorrectnessEvaluator(processList(answers, answerProcessor), range, lossFunction));
    }

    @Override
    public CorrectRangedAnswerFactory withAcceptedRange(Number range) {
        this.range = Math.abs(range.doubleValue());
        return this;
    }

    @Override
    public CorrectRangedAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }
}
