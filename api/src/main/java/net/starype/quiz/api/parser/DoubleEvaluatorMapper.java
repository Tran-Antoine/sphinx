package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class DoubleEvaluatorMapper implements ConfigMapper<Function<AnswerProcessor, AnswerEvaluator>> {

    @Override
    public String getEvaluatorName() {
        return "double-evaluator";
    }

    @Override
    public Function<AnswerProcessor, AnswerEvaluator> map(CommentedConfig config) {
        RangedAnswerFactory factory = new DoubleAnswerFactory()
                .withAcceptedRange(config.getOrElse("answer.evaluator.range", 0.1f))
                .withInterpolation(MATCHER.load("answer.evaluator.interpolation", config).orElse(new LinearLossFunction()));
        return processor -> factory.createCorrectAnswer(
                Answer.fromStringCollection(config.<List<String>>get("answer.correct")),
                processor);
    }



    private static final ConfigMapper<LossFunction> LINEAR_MAPPER = new ConfigMapper<>() {
        @Override
        public String getEvaluatorName() {
            return "linear";
        }

        @Override
        public LossFunction map(CommentedConfig config) {
            return new LinearLossFunction();
        }
    };

    private static final Collection<ConfigMapper<LossFunction>> LOSS_FUNCTIONS = Arrays.asList(
            new ConfigMapper<>() {
                @Override
                public String getEvaluatorName() {
                    return "binary";
                }

                @Override
                public LossFunction map(CommentedConfig config) {
                    return new BinaryLossFunction(config.getOrElse("answer.evaluator.threshold", 0.1));
                }
            },
            LINEAR_MAPPER
    );

    private static final ConfigMatcher<LossFunction> MATCHER = new ConfigMatcher<>(LOSS_FUNCTIONS, LINEAR_MAPPER);
}
