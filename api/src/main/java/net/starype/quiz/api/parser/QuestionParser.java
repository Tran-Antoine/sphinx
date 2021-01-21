package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlParser;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.DefaultAnswerEvaluator;
import net.starype.quiz.api.game.answer.IdentityProcessor;
import net.starype.quiz.api.game.question.DefaultQuestion;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionTag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestionParser {

    private static final ConfigMatcher<Function<AnswerProcessor, AnswerEvaluator>> EVALUATOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new WorldEvaluatorMapper(),
            new DoubleEvaluatorMapper()
    ), new DefaultEvaluatorMapper());

    private static final ConfigMatcher<AnswerProcessor> PROCESSOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new CleanStringProcessorMapper(),
            new CleanSeparatorProcessorMapper()
    ), new CleanStringProcessorMapper());

    public static Question parse(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();

        String rawText = result.get("question.text");
        Set<QuestionTag> tags = result.<List<String>>get("tags")
                .stream()
                .map(QuestionTag::new)
                .collect(Collectors.toSet());

        AnswerProcessor processor = loadProcessor(result);
        AnswerEvaluator evaluator = loadEvaluator(result, processor);

        Question question = new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawAnswer(rawText)
                .withTags(tags)
                .build();

        return question;
    }

    private static AnswerProcessor loadProcessor(CommentedConfig result) {
        AnswerProcessor processor = IdentityProcessor.INSTANCE;
        for(String name : result.<List<String>>get("answer.processors")) {
            Optional<AnswerProcessor> optProcessor = PROCESSOR_MATCHER.load(name, result);
            if(optProcessor.isPresent()) {
                processor = processor.combine(optProcessor.get());
            }
        }
        return processor;
    }

    private static AnswerEvaluator loadEvaluator(CommentedConfig result, AnswerProcessor processor) {
        return EVALUATOR_MATCHER.checkAll("answer.evaluator.name", result).apply(processor);
    }
}
