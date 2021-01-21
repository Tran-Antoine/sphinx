package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.ExactAnswerEvaluator;

public class ExactEvaluatorMapper implements ConfigMapper<PartialEvaluator> {
    @Override
    public String getMapperName() {
        return "default";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        return (answers, processor) -> new ExactAnswerEvaluator(answers);
    }
}
