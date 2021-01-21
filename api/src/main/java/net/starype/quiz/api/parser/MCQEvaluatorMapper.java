package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.MultipleChoiceAnswerFactory;

public class MCQEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "mcq";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        MultipleChoiceAnswerFactory factory = new MultipleChoiceAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
