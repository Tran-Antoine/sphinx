package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerCorrectAnswerFactory implements CorrectRangedAnswerFactory {
    private int range = 0;

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<Answer> answers) {
        return new IntegerCorrectAnswer(new IntegerCorrectnessEvaluator(answers, range));
    }

    @Override
    public CorrectRangedAnswerFactory withAcceptedRange(Number range) {
        this.range = Math.abs(range.intValue()) + 1;
        return this;
    }
}
