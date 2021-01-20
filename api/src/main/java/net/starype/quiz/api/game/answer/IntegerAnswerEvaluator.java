package net.starype.quiz.api.game.answer;

public class IntegerAnswerEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;

    public IntegerAnswerEvaluator(NumberCorrectness correctness) {
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
