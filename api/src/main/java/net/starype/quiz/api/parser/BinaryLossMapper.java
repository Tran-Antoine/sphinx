package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.answer.BinaryLossFunction;
import net.starype.quiz.api.answer.LossFunction;
import net.starype.quiz.api.util.StringUtils;

/**
 * Mapper for the {@link BinaryLossFunction} object
 */
public class BinaryLossMapper implements ConfigMapper<LossFunction> {

    @Override
    public String getMapperName() {
        return "binary";
    }

    @Override
    public LossFunction map(ReadableRawMap config) {
        return new BinaryLossFunction(config.getDouble("answer.evaluator.threshold")
                .orElse(0.1));
    }
}
