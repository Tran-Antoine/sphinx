package net.starype.quiz.api.game.answer;

public class WordAnswerEvaluator implements AnswerEvaluator {

    private ValidityEvaluator validityEvaluator;
    private CorrectnessEvaluator correctnessEvaluator;
    private AnswerProcessor processor;

    public WordAnswerEvaluator(WordCorrectness wordCorrectness, AnswerProcessor processor) {
        this.processor = processor;
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

    @Override
    public AnswerProcessor getProcessor() {
        return processor;
    }
}
