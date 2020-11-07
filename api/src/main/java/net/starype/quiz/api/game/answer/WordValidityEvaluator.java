package net.starype.quiz.api.game.answer;

public class WordValidityEvaluator implements ValidityEvaluator {

    private WordValidityEvaluator()
    {}

    private static WordValidityEvaluator instance = null;

    public static synchronized WordValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new WordValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValid(Answer answer) {
        return answer
                .getAnswer()
                .strip()
                .matches("[a-zA-Z'à-ÿ]+");
    }
}
