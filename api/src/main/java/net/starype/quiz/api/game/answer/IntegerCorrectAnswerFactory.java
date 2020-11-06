package net.starype.quiz.api.game.answer;

import java.util.Set;

public class IntegerCorrectAnswerFactory implements CorrectRangedAnswerFactory {
    private int range = 0;

    @Override
    public CandidateValidityEvaluator getValidityEvaluator() {
        return IntegerCandidateValidityEvaluator.getInstance();
    }

    @Override
    public CorrectAnswer createCorrectAnswer(Set<String> answer) throws RuntimeException {
        if(!answer.stream()
                .allMatch(s ->
                        IntegerCandidateValidityEvaluator.getInstance().isValidCandidate(new Answer(s))
                ))
        {
            throw new RuntimeException("At least one of the proposed answers doesn't satisfy the format specification");
        }

        return new IntegerCorrectAnswer(new IntegerCorrectnessEvaluator(answer, range));
    }

    @Override
    public CorrectRangedAnswerFactory setAcceptedRange(Number range) {
        this.range = range.intValue();
        return this;
    }
}
