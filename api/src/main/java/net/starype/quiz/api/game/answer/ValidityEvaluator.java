package net.starype.quiz.api.game.answer;

/**
 * Represents the second step of the answer parsing pipeline. <br>
 * Once the answer is processed (which is the first step), the validity evaluator object determines
 * whether the latter is considered as 'valid' formatwise. The fact that a given answer is valid does not imply
 * that it's correct. The evaluation of the correctness of an answer is determined by the third and last step of
 * the parsing pipeline, under the condition that the answer passes the validity check
 */
public interface ValidityEvaluator {

    /**
     * Determine whether the provided answer object is valid
     * @param answer the answer
     * @return whether the answer may be evaluated by the last step
     */
    boolean isValid(Answer answer);

}
