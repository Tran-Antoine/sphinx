package net.starype.quiz.api.game.answer;

import java.util.Set;

public class MCQAnswerFactory implements CorrectAnswerFactory {

    private LossFunction lossFunction = new LinearLossFunction();
    private double punitiveRatio = 1.0;

    @Override
    public AnswerEvaluator createCorrectAnswer(Set<Answer> answers, AnswerProcessor answerProcessor) {
        MCQCorrectness multipleChoiceCorrectness = new MCQCorrectness(answers, lossFunction, punitiveRatio);
        return new MCQEvaluator(
                multipleChoiceCorrectness,
                new MCQValidity(),
                answerProcessor.combine(new LowercaseProcessor()));
    }

    public MCQAnswerFactory withInterpolation(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }

    public MCQAnswerFactory withPunitiveRatio(double punitiveRatio) {
        this.punitiveRatio = punitiveRatio;
        return this;
    }
}
