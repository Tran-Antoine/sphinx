package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.CleanSeparatorProcessor;

public class CleanSeparatorProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "clean-separator";
    }

    @Override
    public AnswerProcessor map(CommentedConfig config) {
        return new CleanSeparatorProcessor();
    }
}
