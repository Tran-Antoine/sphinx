package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;
import net.starype.quiz.api.game.answer.BinaryLossFunction;
import net.starype.quiz.api.game.answer.LossFunction;
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
    public LossFunction map(ReadableMap config) {
        return new BinaryLossFunction(StringUtils.mapOptionalNoThrow(config.get("answer.evaluator.threshold"), Double::parseDouble)
                .orElse(0.1));
    }
}
