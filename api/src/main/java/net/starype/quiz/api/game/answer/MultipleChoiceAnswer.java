package net.starype.quiz.api.game.answer;

public class MultipleChoiceAnswer implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;

    public MultipleChoiceAnswer(MultipleChoiceCorrectness multipleChoiceCorrectness) {
        correctnessEvaluator = multipleChoiceCorrectness;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return MultipleChoiceValidity.getInstance();
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
