package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerCorrectAnswerFactory implements CorrectRangedAnswerFactory {
    private int range = 0;

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<String> answer) {
        return new IntegerCorrectAnswer(new IntegerCorrectnessEvaluator(answer, range));
    }

    @Override
    public CorrectRangedAnswerFactory setAcceptedRange(Number range) {
        this.range = Math.abs(range.intValue()) + 1;
        return this;
    }
}
