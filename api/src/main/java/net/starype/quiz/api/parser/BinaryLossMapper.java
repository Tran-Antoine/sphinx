package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.BinaryLossFunction;
import net.starype.quiz.api.game.answer.LossFunction;

public class BinaryLossMapper implements ConfigMapper<LossFunction> {

    @Override
    public String getMapperName() {
        return "binary";
    }

    @Override
    public LossFunction map(CommentedConfig config) {
        return new BinaryLossFunction(config.getOrElse("answer.evaluator.threshold", 0.1));
    }
}
