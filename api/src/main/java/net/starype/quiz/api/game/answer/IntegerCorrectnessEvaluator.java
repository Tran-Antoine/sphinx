package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerCorrectnessEvaluator implements CorrectnessEvaluator {

    private final Set<Answer> acceptedAnswers;
    private final int acceptedRange;
    private final LossFunction lossFunction;

    public IntegerCorrectnessEvaluator(Set<Answer> acceptedAnswers, int acceptedRange, LossFunction lossFunction) {
        this.acceptedAnswers = acceptedAnswers;
        this.acceptedRange = Math.max(Math.abs(acceptedRange), 1);
        this.lossFunction = lossFunction;
    }

    @Override
    public double getCorrectness(Answer answer) {
        int proposedAnswer = answer.asInt();
        double closeness = acceptedAnswers.stream()
                .map(accepted -> Math.abs(proposedAnswer - accepted.asInt()))
                .filter(n -> n < acceptedRange)
                .min(Integer::compareTo)
                .orElse(acceptedRange + 1);
        if(closeness >= acceptedRange + 1)
            return 0.0;
        return lossFunction.evaluate(closeness, 0.0, acceptedRange);
    }
}
