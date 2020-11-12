package net.starype.quiz.api.game.answer;

public class IntegerAnswer implements CorrectAnswer {

    private CorrectnessEvaluator correctnessEvaluator;

    public IntegerAnswer(NumberCorrectness correctness) {
        correctnessEvaluator = correctness;
    }

    @Override
    public ValidityEvaluator getValidityEvaluator() {
        return IntegerValidity.getInstance();
    }

    @Override
    public CorrectnessEvaluator getCorrectnessEvaluator() {
        return correctnessEvaluator;
    }
}
