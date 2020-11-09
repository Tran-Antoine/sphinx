package net.starype.quiz.api.game.answer;

public class DoubleCorrectAnswer implements CorrectAnswer {

    private CorrectnessEvaluator correctnessEvaluator;

    public DoubleCorrectAnswer(DoubleCorrectnessEvaluator integerCorrectnessEvaluator) {
        correctnessEvaluator = integerCorrectnessEvaluator;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return DoubleValidityEvaluator.getInstance();
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }

}
