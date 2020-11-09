package net.starype.quiz.api.game.answer;

public class DoubleValidityEvaluator implements ValidityEvaluator {
    private DoubleValidityEvaluator() {}

    private static DoubleValidityEvaluator instance = null;

    public static DoubleValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new DoubleValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValid(Answer answer) {
        return answer
                .getAnswerText()
                .strip()
                .matches("^[+-]?[0-9]*[,.]?[0-9]*$");
    }
}
