package net.starype.quiz.api.game.answer;

import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Set;
import java.util.stream.Collectors;

public class WordCorrectnessEvaluator implements CorrectnessEvaluator {
    private Set<String> acceptedAnswers;

    public WordCorrectnessEvaluator(Set<String> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers.stream()
                .map(
                        s -> s.strip().toLowerCase()
                )
                .filter(
                        s -> WordCandidateValidityEvaluator.getInstance()
                                .isValidCandidate(new Answer(s))
                )
                .collect(Collectors.toSet());
    }

    @Override
    public double getCorrectness(Answer answer) throws RuntimeException {
        if(!WordCandidateValidityEvaluator
                .getInstance()
                .isValidCandidate(answer)) {
            throw new RuntimeException("The given answer doesn't satisfy formats specifications");
        }
        return acceptedAnswers.contains(
                    answer.getAnswer().strip().toLowerCase()
                ) ? (1.0) : (0.0);
    }
}
