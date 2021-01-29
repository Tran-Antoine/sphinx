package net.starype.quiz.api.game.answer;

/**
 * The first step of the answer evaluation pipeline.
 * Answer processors modify input answers to properly format them, if possible.
 * For further information, see {@code AnswerEvaluator}.
 * @see AnswerEvaluator
 */
public interface AnswerProcessor {

    /**
     * Modify the entry and store the result in a new {@code Answer} object.
     * @param answer the given entry
     * @return a new {@code Answer} object storing the computed result
     */
    Answer process(Answer answer);

    /**
     * Wrap the string into an {@code Answer} object and process it.
     * @see #process(Answer)
     * @param str the given string
     * @return a new {@code Answer} object storing the computed result
     */
    default Answer process(String str) {
        return process(Answer.fromString(str));
    }

    /**
     * Combine the current processor with another one. <br>
     * Example: {@code a.combine(b)} will result in a processor that first processes through {@code b}, then through {@code a}
     * @param processor the other processor to combine with the current one
     * @return a new processor that corresponds to function composition of {@code this} and {@code processor}
     */
    default AnswerProcessor combine(AnswerProcessor processor) {
        return (answer) -> this.process(processor.process(answer));
    }

}
