package net.starype.quiz.api.game.answer;

public class IntegerValidity implements ValidityEvaluator {

    private IntegerValidity() {}

    private static IntegerValidity instance = null;

    public static IntegerValidity getInstance()
    {
        if(instance == null) {
            instance = new IntegerValidity();
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
