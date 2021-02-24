package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.answer.WordAnswerFactory;

/**
 * Mapper for the {@link net.starype.quiz.api.answer.WordAnswerEvaluator} object
 */
public class WordEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "word";
    }

    @Override
    public PartialEvaluator map(ReadableRawMap config) {
        WordAnswerFactory factory = new WordAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
