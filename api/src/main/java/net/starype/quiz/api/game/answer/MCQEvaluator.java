package net.starype.quiz.api.game.answer;

public class MCQEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;
    private ValidityEvaluator validity;
    private AnswerProcessor processor;

    public MCQEvaluator(MCQCorrectness multipleChoiceCorrectness, MCQValidity validity, AnswerProcessor processor) {
        correctnessEvaluator = multipleChoiceCorrectness;
        this.validity = validity;
        this.processor = processor;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return validity;
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
