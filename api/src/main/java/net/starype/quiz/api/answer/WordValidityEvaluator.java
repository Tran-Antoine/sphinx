package net.starype.quiz.api.answer;

public class WordValidityEvaluator implements ValidityEvaluator {

    private WordValidityEvaluator() {}

    private static WordValidityEvaluator instance = null;

    public static WordValidityEvaluator getInstance()
    {
        if(instance == null) {
            instance = new WordValidityEvaluator();
        }
        return instance;
    }

    @Override
    public boolean isValid(Answer answer) {
        return answer
                .getAnswerText()
                .matches("[a-zA-Z'0-9\\-]+");
    }
}
