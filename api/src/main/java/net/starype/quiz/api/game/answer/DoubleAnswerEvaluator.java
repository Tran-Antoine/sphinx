package net.starype.quiz.api.game.answer;

public class DoubleAnswerEvaluator implements AnswerEvaluator {

    private CorrectnessEvaluator correctnessEvaluator;

    public DoubleAnswerEvaluator(NumberCorrectness integerCorrectnessEvaluator) {
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
