package net.starype.quiz.api.game.answer;

import java.util.Collection;

public class MCQValidity implements ValidityEvaluator {

    private Collection<? extends Answer> answers;

    public MCQValidity(Collection<? extends Answer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean isValid(Answer answer) {
        return answers.containsAll(answer.split(";"));
    }
}
