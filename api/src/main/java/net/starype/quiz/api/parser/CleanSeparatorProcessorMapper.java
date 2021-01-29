package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.CleanSeparatorProcessor;

/**
 * Mapper for the {@link CleanSeparatorProcessor} object
 */
public class CleanSeparatorProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "clean-separator";
    }

    @Override
    public AnswerProcessor map(ReadableMap config) {
        return new CleanSeparatorProcessor();
    }
}
