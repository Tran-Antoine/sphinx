package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.CleanStringProcessor;

/**
 * Mapper for the {@link CleanStringProcessor} object
 */
public class CleanStringProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "clean-string";
    }

    @Override
    public AnswerProcessor map(ReadableRawMap config) {
        return new CleanStringProcessor();
    }
}
