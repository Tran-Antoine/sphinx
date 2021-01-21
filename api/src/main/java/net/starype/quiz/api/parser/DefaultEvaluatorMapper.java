package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.DefaultAnswerEvaluator;

import java.util.List;
import java.util.function.Function;

public class DefaultEvaluatorMapper implements ConfigMapper<Function<AnswerProcessor, AnswerEvaluator>> {
    @Override
    public String getEvaluatorName() {
        return "default";
    }

    @Override
    public Function<AnswerProcessor, AnswerEvaluator> map(CommentedConfig config) {
        return (processor) -> new DefaultAnswerEvaluator(Answer.fromStringCollection(config.<List<String>>get("answer.correct")));
    }
}
