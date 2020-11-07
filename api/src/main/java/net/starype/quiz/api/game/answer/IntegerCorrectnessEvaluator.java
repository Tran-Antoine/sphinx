package net.starype.quiz.api.game.answer;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IntegerCorrectnessEvaluator implements CorrectnessEvaluator {
    private Set<Integer> acceptedAnswers;
    private int acceptedRange;

    public IntegerCorrectnessEvaluator(Set<String> acceptedAnswers, int acceptedRange) {
        this.acceptedAnswers = acceptedAnswers.stream()
                .filter(
                        s -> IntegerCandidateValidityEvaluator.getInstance()
                                .isValidCandidate(new Answer(s))
                )
                .map(
                        s -> Integer.valueOf(s.strip())
                )
                .collect(Collectors.toSet());
        this.acceptedRange = Math.max(Math.abs(acceptedRange), 1);
    }

    @Override
    public double getCorrectness(Answer answer) throws RuntimeException {
        if(!IntegerCandidateValidityEvaluator
                .getInstance()
                .isValidCandidate(answer)) {
            throw new RuntimeException("The given answer doesn't satisfy formats specifications");
        }
        int proposedAnswer = Integer.valueOf(answer.getAnswer().strip());
        return (double) acceptedAnswers.stream()
                .map(n -> (int) Math.abs(proposedAnswer - n))
                .filter(n -> n < acceptedRange)
                .min(Integer::compareTo)
                .map(n -> Math.abs(acceptedRange - n))
                .orElse(0) / (double) (acceptedRange);
    }
}
