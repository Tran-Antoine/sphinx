package net.starype.quiz.api.game.answer;

public class IntegerCorrectAnswer implements CorrectAnswer {

    private CorrectnessEvaluator correctnessEvaluator;

    public IntegerCorrectAnswer(IntegerCorrectnessEvaluator integerCorrectnessEvaluator) {
        correctnessEvaluator = integerCorrectnessEvaluator;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidityEvaluator.getInstance();
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
