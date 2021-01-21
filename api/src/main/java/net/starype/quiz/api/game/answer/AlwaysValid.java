package net.starype.quiz.api.game.answer;

public class AlwaysValid implements ValidityEvaluator {

    @Override
    public boolean isValid(Answer answer) {
        return true;
    }
}
