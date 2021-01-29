package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.WordAnswerFactory;

/**
 * Mapper for the {@link net.starype.quiz.api.game.answer.WordAnswerEvaluator} object
 */
public class WordEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "word";
    }

    @Override
    public PartialEvaluator map(ReadableMap config) {
        WordAnswerFactory factory = new WordAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
