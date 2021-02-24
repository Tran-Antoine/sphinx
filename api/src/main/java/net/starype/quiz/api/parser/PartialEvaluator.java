package net.starype.quiz.api.parser;

import net.starype.quiz.api.answer.Answer;
import net.starype.quiz.api.answer.AnswerEvaluator;
import net.starype.quiz.api.answer.AnswerProcessor;

import java.util.Set;

/**
 * Represents a partially constructed {@link AnswerEvaluator}, missing a set of answers and an answer processor. <br>
 */
public interface PartialEvaluator {

    /**
     * Define the required procedure to create an answer evaluator with given set of answers and processor
     * @param answers the set of answers provided
     * @param processor the processor provided
     * @return an answer evaluator matching the provided parameters
     */
    AnswerEvaluator create(Set<Answer> answers, AnswerProcessor processor);
}
