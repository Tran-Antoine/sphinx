package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.BinaryLossFunction;
import net.starype.quiz.api.game.answer.WordAnswerFactory;

/**
 * Mapper for the {@link net.starype.quiz.api.game.answer.WordAnswerEvaluator} object
 */
public class WordEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "word";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        WordAnswerFactory factory = new WordAnswerFactory();
        return factory::createCorrectAnswer;
    }
}
