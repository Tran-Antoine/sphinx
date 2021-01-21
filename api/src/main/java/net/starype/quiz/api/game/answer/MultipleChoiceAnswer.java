package net.starype.quiz.api.game.answer;

public class MultipleChoiceAnswer implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;
    private ValidityEvaluator validity;

    public MultipleChoiceAnswer(MultipleChoiceCorrectness multipleChoiceCorrectness, MultipleChoiceValidity validity) {
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
