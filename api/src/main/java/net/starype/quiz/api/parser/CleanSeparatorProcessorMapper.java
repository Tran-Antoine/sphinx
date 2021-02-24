package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.answer.AnswerProcessor;
import net.starype.quiz.api.answer.CleanSeparatorProcessor;

/**
 * Mapper for the {@link CleanSeparatorProcessor} object
 */
public class CleanSeparatorProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "clean-separator";
    }

    @Override
    public AnswerProcessor map(ReadableRawMap config) {
        return new CleanSeparatorProcessor();
    }
}
