package net.starype.quiz.api.game.answer;

import java.util.Set;

public class DefaultAnswerEvaluator implements AnswerEvaluator {

    private Set<Answer> answers;

    public DefaultAnswerEvaluator(Set<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return new ExactAnswerCorrectness(answers);
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return new AlwaysValid();
    }
}
