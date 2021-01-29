package net.starype.quiz.api.game.answer;

/**
 * An evaluator that never fails.
 */
public class AlwaysValid implements ValidityEvaluator {

    /**
     * @return true, regardless of the answer
     */
    @Override
    public boolean isValid(Answer answer) {
        return true;
    }
}
