package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.ExactAnswerEvaluator;

/**
 * Mapper for the {@link ExactAnswerEvaluator} object
 */
public class ExactEvaluatorMapper implements ConfigMapper<PartialEvaluator> {
    @Override
    public String getMapperName() {
        return "exact-answer";
    }

    @Override
    public PartialEvaluator map(ReadableMap config) {
        return (answers, processor) -> new ExactAnswerEvaluator(answers);
    }
}
