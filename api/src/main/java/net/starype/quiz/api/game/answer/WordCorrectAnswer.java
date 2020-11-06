package net.starype.quiz.api.game.answer;

public class WordCorrectAnswer implements CorrectAnswer {
    private CandidateValidityEvaluator candidateValidityEvaluator;
    private CorrectnessEvaluator correctnessEvaluator;

    public WordCorrectAnswer(WordCorrectnessEvaluator wordCorrectnessEvaluator) {
        candidateValidityEvaluator = WordCandidateValidityEvaluator.getInstance();
        correctnessEvaluator = (CorrectnessEvaluator) wordCorrectnessEvaluator;
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
