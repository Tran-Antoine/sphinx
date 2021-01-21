package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.WordAnswerFactory;

public class WorldEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getEvaluatorName() {
        return "WordEvaluator";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        WordAnswerFactory factory = new WordAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
