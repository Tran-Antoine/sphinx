package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.DefaultAnswerEvaluator;

public class DefaultEvaluatorMapper implements ConfigMapper<PartialEvaluator> {
    @Override
    public String getEvaluatorName() {
        return "default";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        return (answers, processor) -> new DefaultAnswerEvaluator(answers);
    }
}
