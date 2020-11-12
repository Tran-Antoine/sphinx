package net.starype.quiz.api.game.answer;

public class MultipleChoiceValidity implements ValidityEvaluator {

    private static MultipleChoiceValidity instance = null;

    private MultipleChoiceValidity() {

    }

    public static synchronized MultipleChoiceValidity getInstance() {
        if(instance == null) {
            instance = new MultipleChoiceValidity();
        }
        return instance;
    }

    @Override
    public boolean isValid(Answer answer) {
        return answer.getAnswerText()
                .matches("[a-zA-Z0-9 \\-\\|\\\\\\;]+");
    }
}
