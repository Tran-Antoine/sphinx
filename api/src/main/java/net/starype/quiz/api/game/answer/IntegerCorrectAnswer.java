package net.starype.quiz.api.game.answer;

public class IntegerCorrectAnswer implements CorrectAnswer {
    private CandidateValidityEvaluator candidateValidityEvaluator;
    private CorrectnessEvaluator correctnessEvaluator;

    public IntegerCorrectAnswer(IntegerCorrectnessEvaluator integerCorrectnessEvaluator) {
        candidateValidityEvaluator = WordCandidateValidityEvaluator.getInstance();
        correctnessEvaluator = (CorrectnessEvaluator) integerCorrectnessEvaluator;
    }

    @Override
    public CandidateValidityEvaluator getCandidateValidityEvaluator() {
        return candidateValidityEvaluator;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
