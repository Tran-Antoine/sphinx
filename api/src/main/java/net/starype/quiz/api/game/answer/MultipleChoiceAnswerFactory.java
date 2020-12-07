package net.starype.quiz.api.game.answer;

import java.util.Set;

public class MultipleChoiceAnswerFactory implements CorrectAnswerFactory {
    private LossFunction lossFunction = new LinearLossFunction();
    private double punitiveRatio = 1.0;

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        MultipleChoiceCorrectness multipleChoiceCorrectness
                = new MultipleChoiceCorrectness(answers, lossFunction, punitiveRatio);
        return new MultipleChoiceAnswer(multipleChoiceCorrectness);
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return MultipleChoiceValidity.getInstance();
    }

    public MultipleChoiceAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }

    public MultipleChoiceAnswerFactory withPunitiveRatio(double punitiveRatio) {
        this.punitiveRatio = punitiveRatio;
        return this;
    }
}
