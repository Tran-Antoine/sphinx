package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.LinearLossFunction;
import net.starype.quiz.api.game.answer.LossFunction;

public class LinearLossMapper implements ConfigMapper<LossFunction> {

    @Override
    public String getMapperName() {
        return "linear";
    }

    @Override
    public LossFunction map(CommentedConfig config) {
        return new LinearLossFunction();
    }
}
