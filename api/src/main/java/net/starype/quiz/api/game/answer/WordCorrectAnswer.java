package net.starype.quiz.api.game.answer;

public class WordCorrectAnswer implements CorrectAnswer {
    private ValidityEvaluator validityEvaluator;
    private CorrectnessEvaluator correctnessEvaluator;

    public WordCorrectAnswer(WordCorrectnessEvaluator wordCorrectnessEvaluator) {
        validityEvaluator = WordValidityEvaluator.getInstance();
        correctnessEvaluator = wordCorrectnessEvaluator;
    }

    @Override
    public ValidityEvaluator getCandidateValidityEvaluator() {
        return validityEvaluator;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
