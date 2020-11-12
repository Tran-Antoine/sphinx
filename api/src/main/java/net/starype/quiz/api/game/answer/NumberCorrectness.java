package net.starype.quiz.api.game.answer;

import java.util.Set;

public class NumberCorrectness implements CorrectnessEvaluator {

    private final Set<Answer> acceptedAnswers;
    private final double acceptedRange;
    private final LossFunction lossFunction;

    public NumberCorrectness(Set<Answer> acceptedAnswers, double acceptedRange, LossFunction lossFunction) {
        this.acceptedAnswers = acceptedAnswers;
        this.acceptedRange = Math.max(Math.abs(acceptedRange), 0.0000001);
        this.lossFunction = lossFunction;
    }

    @Override
    public double getCorrectness(Answer answer) {
        double proposedAnswer = answer.asDouble();
        double closeness = acceptedAnswers.stream()
                .map(accepted -> Math.abs(proposedAnswer - accepted.asDouble()))
                .filter(n -> n < acceptedRange)
                .min(Double::compareTo)
                .orElse(acceptedRange + 1.0);
        if(closeness >= acceptedRange + 1.0)
            return 0.0;
        return lossFunction.evaluate(closeness, 0.0, acceptedRange);
    }

}
