package net.starype.quiz.api.game.answer;

public class DoubleAnswerEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;
    private AnswerProcessor processor;

    public DoubleAnswerEvaluator(NumberCorrectness integerCorrectnessEvaluator, AnswerProcessor processor) {
        correctnessEvaluator = integerCorrectnessEvaluator;
        this.processor = processor;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return DoubleValidity.getInstance();
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
