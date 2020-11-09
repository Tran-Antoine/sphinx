package net.starype.quiz.api.game.answer;

public class DoubleValidity implements ValidityEvaluator {
    private DoubleValidity() {}

    private static DoubleValidity instance = null;

    public static DoubleValidity getInstance()
    {
        if(instance == null) {
            instance = new DoubleValidity();
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
