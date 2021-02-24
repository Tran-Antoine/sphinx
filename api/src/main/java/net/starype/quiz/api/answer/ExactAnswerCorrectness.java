package net.starype.quiz.api.answer;

import java.util.Set;

public class ExactAnswerCorrectness implements CorrectnessEvaluator {

    private Set<Answer> answers;

    public ExactAnswerCorrectness(Set<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public double getCorrectness(Answer answer) {
        return answers.contains(answer) ? 1.0 : 0;
    }
}
