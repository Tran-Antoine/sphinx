package net.starype.quiz.api.game.answer;

public class WordAnswer implements CorrectAnswer {

    private ValidityEvaluator validityEvaluator;
    private CorrectnessEvaluator correctnessEvaluator;

    public WordAnswer(WordCorrectness wordCorrectness) {
        validityEvaluator = WordValidityEvaluator.getInstance();
        correctnessEvaluator = wordCorrectness;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return validityEvaluator;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
