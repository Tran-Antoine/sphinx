package net.starype.quiz.api.game.answer;

public class IntegerValidityEvaluator implements ValidityEvaluator {

    private IntegerValidityEvaluator() {}

    private static IntegerValidityEvaluator instance = null;

    public static IntegerValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new IntegerValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValid(Answer answer) {
        try {
            answer.asInt();
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
