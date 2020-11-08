package net.starype.quiz.api.game.answer;

import java.util.Set;
import java.util.stream.Collectors;

public class IntegerCorrectnessEvaluator implements CorrectnessEvaluator {

    private Set<Answer> acceptedAnswers;
    private int acceptedRange;

    public IntegerCorrectnessEvaluator(Set<Answer> acceptedAnswers, int acceptedRange) {
        this.acceptedAnswers = acceptedAnswers;
        this.acceptedRange = Math.max(Math.abs(acceptedRange), 1);
    }

    @Override
    public double getCorrectness(Answer answer) {
        int proposedAnswer = answer.asInt();
        double closeness = acceptedAnswers.stream()
                .map(accepted -> Math.abs(proposedAnswer - accepted.asInt()))
                .filter(n -> n < acceptedRange)
                .min(Integer::compareTo)
                .map(n -> Math.abs(acceptedRange - n))
                .orElse(0);
        return linearMapping(closeness, acceptedRange);
    }

    private double linearMapping(double closeness, double maxCloseness) {
        return closeness / maxCloseness;
    }
}
