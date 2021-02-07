package net.starype.quiz.api.game.answer;

/**
 * Core object of the answer evaluation pipeline.
 * This encapsulates all three steps of the evaluation process, used to determine how accurate a given answer is.
 * Unlike score evaluation which can vary because of external factors such as time or round ranking, answer accuracy
 * is said to be absolute, meaning that regardless of the context, if the same answer is provided twice to the
 * evaluator, the two computed accuracies should be theoretically identical. <br>
 * <br>
 * The 3 steps of the pipeline are the following: <br>
 *  <ol type="1">
 *   <li>
 *       <b>Answer processing</b> <br>
 *       All answers should be "reformulated" by the evaluator first. For instance, if only a two-digit precision is
 *       required, all double answers should be rounded before verified, or else {@code 4.291} would be considered "wrong" in
 *       case the correct answer is {@code 4.29}. Another example is true-false style questions. In order for {@code T} or
 *       {@code Yes} to be
 *       interpreted as {@code True}, the answer should be processed. For this case in particular, it would be possible to
 *       not process the answer but rather use a list of all possible answers, however this system allows more flexibility.
 *       By default, answers are simply processed by getting 'cleaned' (trailing spaces and double spaces removed). <br>
 *       Step represented by {@code AnswerProcessor}
 *   </li>
 *   <li>
 *       <b>Validity check</b> <br>
 *       Once the answer is properly processed, the evaluator needs to ensure that the answer is 'valid'. In this context,
 *       {@code valid} and {@code correct} are very different terms. An answer is valid if its <b>format</b> is correct.
 *       For instance, assuming {@code 5.78} is the correct answer, {@code 90.1} would be a valid (yet incorrect) answer,
 *       whereas {@code hello} would be invalid.
 *       Another example is MCQ (multiple-choice question). Assuming there are 4 possible answers (A, B, C, D), any answer that's not
 *       either of these four would be invalid. <br>
 *       The idea behind this step is that some rounds may behave differently if the player's answer is incorrect or invalid.
 *       Typically, so-called 'soft' rounds would not count the guess as wrong in case it's invalid, allowing the player to retry. <br>
 *       Step represented by {@code ValidityEvaluator}
 *   </li>
 *   <li>
 *       <b>Answer evaluation</b> <br>
 *       Now that the answer has been processed and is known to be valid, the last step of the pipeline takes care of
 *       evaluating the accuracy (between {@code 0.0} and {@code 1.0}) of the answer. Several implementations are available,
 *       such as {@link ExactAnswerEvaluator}, {@link WordAnswerEvaluator}, or {@link DoubleAnswerEvaluator}. For double-style
 *       answer evaluation, there are multiple implementations of loss functions, such as {@link BinaryLossFunction} or
 *       {@link LinearLossFunction}. <br>
 *       Step represented by {@code CorrectnessEvaluator}
 *   </li>
 * </ol>
 * Note that most evaluators provided by the API should be created from their respective factory, and not directly.
 * @see AnswerProcessor
 * @see ValidityEvaluator
 * @see CorrectnessEvaluator
 */
public interface AnswerEvaluator {

    /**
     * Retrieve the third step of the process, used to evaluate the accuracy of the processed and valid answer.
     * @return the {@code CorrectnessEvaluator} object
     */
    CorrectnessEvaluator getCorrectnessEvaluator();

    /**
     * Retrieve the second step of the process, used to verify that a processed answer is valid.
     * @return the {@code ValidityEvaluator} object
     */
    ValidityEvaluator getValidityEvaluator();

    /**
     * Retrieve the first step of the process, used to process a raw answer.
     * @return the {@code AnswerProcessor} object
     */
    AnswerProcessor getProcessor();

}
