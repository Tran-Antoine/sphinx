package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.game.answer.*;
import net.starype.quiz.api.util.StringUtils;

import java.util.Arrays;

/**
 * Mapper for the {@link DoubleAnswerEvaluator} object
 */
public class DoubleEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    /**
     * Matcher containing the different loss functions
     */
    public static final ConfigMatcher<LossFunction> LOSS_FUNCTIONS_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new BinaryLossMapper(),
            new LinearLossMapper()
    ), new LinearLossMapper());


    @Override
    public String getMapperName() {
        return "double-evaluator";
    }

    @Override
    public PartialEvaluator map(ReadableRawMap config) {
        RangedAnswerFactory factory = new DoubleAnswerFactory()
                .withAcceptedRange(StringUtils.mapOptionalNoThrow(config.get("answer.evaluator.range"), Float::parseFloat).orElse(0.1f))
                .withInterpolation(LOSS_FUNCTIONS_MATCHER.loadFromKey("answer.evaluator.interpolation", config).orElse(new LinearLossFunction()));
        return factory::createCorrectAnswer;
    }
}
