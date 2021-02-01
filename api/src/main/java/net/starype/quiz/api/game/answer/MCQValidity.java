package net.starype.quiz.api.game.answer;

import java.util.Collection;
import java.util.stream.Collectors;

public class MCQValidity implements ValidityEvaluator {

    private Collection<? extends Answer> answers;

    public MCQValidity(Collection<? extends Answer> answers) {
        this.answers = answers
                .stream()
                .map(answer -> answer.mapText(String::toLowerCase))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Answer answer) {
        return answers.containsAll(answer
                .split(";")
                .stream()
                .map(a -> a.mapText(String::toLowerCase))
                .collect(Collectors.toSet()));
    }
}
