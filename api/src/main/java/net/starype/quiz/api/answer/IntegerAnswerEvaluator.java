package net.starype.quiz.api.answer;

public class IntegerAnswerEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;
    private AnswerProcessor processor;

    public IntegerAnswerEvaluator(NumberCorrectness correctness, AnswerProcessor processor) {
        correctnessEvaluator = correctness;
        this.processor = processor;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidity.getInstance();
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
