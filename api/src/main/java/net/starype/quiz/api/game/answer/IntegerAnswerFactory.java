package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerAnswerFactory implements RangedAnswerFactory {
    private int range = 1;
    private LossFunction lossFunction = new LinearLossFunction();

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidity.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new IntegerAnswer(new NumberCorrectness(processList(answers, answerProcessor), range, lossFunction));
    }

    @Override
    public RangedAnswerFactory withAcceptedRange(Number range) {
        this.range = Math.abs(range.intValue());
        return this;
    }

    @Override
    public RangedAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }
}
