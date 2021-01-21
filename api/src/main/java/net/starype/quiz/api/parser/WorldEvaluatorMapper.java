package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.WordAnswerFactory;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class WorldEvaluatorMapper implements ConfigMapper<Function<AnswerProcessor, AnswerEvaluator>> {

    @Override
    public String getEvaluatorName() {
        return "WordEvaluator";
    }

    @Override
    public Function<AnswerProcessor, AnswerEvaluator> map(CommentedConfig config) {
        Set<Answer> correctAnswers = Answer.fromStringCollection(config.<List<String>>get("answer.correct"));
        return processor -> new WordAnswerFactory().createCorrectAnswer(correctAnswers, processor);
    }
}
