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
        try {
            answer.asDouble();
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
