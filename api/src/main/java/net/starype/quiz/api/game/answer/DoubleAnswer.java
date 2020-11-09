package net.starype.quiz.api.game.answer;

public class DoubleAnswer implements CorrectAnswer {

    private CorrectnessEvaluator correctnessEvaluator;

    public DoubleAnswer(NumberCorrectness integerCorrectnessEvaluator) {
        correctnessEvaluator = integerCorrectnessEvaluator;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return DoubleValidity.getInstance();
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }

}
