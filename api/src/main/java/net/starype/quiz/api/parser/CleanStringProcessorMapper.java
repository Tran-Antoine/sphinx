package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.CleanStringProcessor;

public class CleanStringProcessorMapper implements ConfigMapper<AnswerProcessor> {

    @Override
    public String getEvaluatorName() {
        return "clean-string";
    }

    @Override
    public AnswerProcessor map(CommentedConfig config) {
        return new CleanStringProcessor();
    }
}
