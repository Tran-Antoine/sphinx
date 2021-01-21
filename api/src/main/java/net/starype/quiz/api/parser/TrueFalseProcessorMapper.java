package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.TrueFalseProcessor;

public class TrueFalseProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getMapperName() {
        return "true-false";
    }

    @Override
    public AnswerProcessor map(CommentedConfig config) {
        return new TrueFalseProcessor();
    }
}
