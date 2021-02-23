package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.answer.AnswerProcessor;
import net.starype.quiz.api.answer.TrueFalseProcessor;

/**
 * Mapper for the {@link TrueFalseProcessor} object
 */
public class TrueFalseProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "true-false";
    }

    @Override
    public AnswerProcessor map(ReadableRawMap config) {
        return new TrueFalseProcessor();
    }
}
