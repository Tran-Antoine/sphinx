package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;

public interface ConfigMapper<R> {

    String getEvaluatorName();
    R map(CommentedConfig config);
}
