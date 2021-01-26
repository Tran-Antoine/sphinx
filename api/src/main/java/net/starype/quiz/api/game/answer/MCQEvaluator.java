package net.starype.quiz.api.game.answer;

public class MCQEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;
    private ValidityEvaluator validity;

    public MCQEvaluator(MCQCorrectness multipleChoiceCorrectness, MCQValidity validity) {
        correctnessEvaluator = multipleChoiceCorrectness;
        this.validity = validity;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return validity;
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
