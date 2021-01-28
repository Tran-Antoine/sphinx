package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.TrueFalseProcessor;

/**
 * Mapper for the {@link TrueFalseProcessor} object
 */
public class TrueFalseProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "true-false";
    }

    @Override
    public AnswerProcessor map(ReadableMap config) {
        return new TrueFalseProcessor();
    }
}
