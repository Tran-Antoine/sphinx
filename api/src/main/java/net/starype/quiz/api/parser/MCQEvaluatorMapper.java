package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.MCQEvaluator;
import net.starype.quiz.api.game.answer.MCQAnswerFactory;

/**
 * Mapper for the {@link MCQEvaluator} object
 */
public class MCQEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "mcq";
    }

    @Override
    public PartialEvaluator map(ReadableMap config) {
        MCQAnswerFactory factory = new MCQAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
