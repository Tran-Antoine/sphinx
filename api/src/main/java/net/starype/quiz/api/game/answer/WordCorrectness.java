package net.starype.quiz.api.game.answer;

import java.util.Set;
import java.util.stream.Collectors;

public class WordCorrectness implements CorrectnessEvaluator {

    private Set<Answer> acceptedAnswers;

    public WordCorrectness(Set<Answer> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers
                .stream()
                .filter(WordValidityEvaluator.getInstance()::isValid)
                .collect(Collectors.toSet());
    }

    @Override
    public double getCorrectness(Answer answer) {
        return acceptedAnswers.contains(answer) ? (1.0) : (0.0);
    }
}
