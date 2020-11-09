package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerCorrectAnswerFactory implements CorrectRangedAnswerFactory {
    private int range = 1;
    private LossFunction lossFunction = new LinearLossFunction();

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        return new IntegerCorrectAnswer(new IntegerCorrectnessEvaluator(processList(answers, answerProcessor), range, lossFunction));
    }

    @Override
    public CorrectRangedAnswerFactory withAcceptedRange(Number range) {
        this.range = Math.abs(range.intValue());
        return this;
    }

    @Override
    public CorrectRangedAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }
}
