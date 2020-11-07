package net.starype.quiz.api.game.answer;

import java.util.Set;
import java.util.stream.Collectors;

public class WordCorrectnessEvaluator implements CorrectnessEvaluator {
    private Set<String> acceptedAnswers;

    public WordCorrectnessEvaluator(Set<String> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers.stream()
                .map(s -> s.strip().toLowerCase())
                .filter(s -> WordValidityEvaluator.getInstance().isValid(new Answer(s)))
                .collect(Collectors.toSet());
    }

    @Override
    public double getCorrectness(Answer answer) {
        return acceptedAnswers.contains(answer.getAnswer().strip().toLowerCase()) ? (1.0) : (0.0);
    }
}
