package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.*;

import java.util.Arrays;
import java.util.Collection;

public class DoubleEvaluatorMapper implements ConfigMapper<PartialEvaluator> {

    public static final ConfigMatcher<LossFunction> NUMBER_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new BinaryLossMapper(),
            new LinearLossMapper()
    ), new LinearLossMapper());


    @Override
    public String getMapperName() {
        return "double-evaluator";
    }

    @Override
    public PartialEvaluator map(CommentedConfig config) {
        RangedAnswerFactory factory = new DoubleAnswerFactory()
                .withAcceptedRange(config.getOrElse("answer.evaluator.range", 0.1f))
                .withInterpolation(NUMBER_MATCHER.load("answer.evaluator.interpolation", config).orElse(new LinearLossFunction()));
        return factory::createCorrectAnswer;
    }
}
