package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.answer.IntegerAnswerEvaluator;
import net.starype.quiz.api.answer.IntegerAnswerFactory;
import net.starype.quiz.api.answer.LinearLossFunction;
import net.starype.quiz.api.answer.RangedAnswerFactory;
import net.starype.quiz.api.util.StringUtils;

/**
 * Mapper for the {@link IntegerAnswerEvaluator} object
 */
public class IntegerEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    @Override
    public String getMapperName() {
        return "integer";
    }

    @Override
    public PartialEvaluator map(ReadableRawMap config) {
        RangedAnswerFactory factory = new IntegerAnswerFactory()
                .withAcceptedRange(config.getFloat("answer.evaluator.range").orElse(0.1f))
                .withInterpolation(DoubleEvaluatorMapper.LOSS_FUNCTIONS_MATCHER.loadFromKey("answer.evaluator.interpolation", config).orElse(new LinearLossFunction()));
        return factory::createCorrectAnswer;
    }
}
