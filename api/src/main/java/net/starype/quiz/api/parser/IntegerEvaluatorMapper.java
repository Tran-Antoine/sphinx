package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.IntegerAnswerEvaluator;
import net.starype.quiz.api.game.answer.IntegerAnswerFactory;
import net.starype.quiz.api.game.answer.LinearLossFunction;
import net.starype.quiz.api.game.answer.RangedAnswerFactory;

/**
 * Mapper for the {@link IntegerAnswerEvaluator} object
 */
public class IntegerEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "integer";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        RangedAnswerFactory factory = new IntegerAnswerFactory()
                .withAcceptedRange(config.getOrElse("answer.evaluator.range", 0.1f))
                .withInterpolation(DoubleEvaluatorMapper.LOSS_FUNCTIONS_MATCHER.loadFromKey("answer.evaluator.interpolation", config).orElse(new LinearLossFunction()));
        return factory::createCorrectAnswer;
    }
}
