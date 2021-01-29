package net.starype.quiz.api.game.answer;

/**
 * An evaluator that never fails.
 */
public class AlwaysValid implements ValidityEvaluator {

    @Override
    public boolean isValid(Answer answer) {
        return true;
    }
}
