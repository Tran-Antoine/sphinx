package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlParser;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.IdentityProcessor;
import net.starype.quiz.api.game.question.DefaultQuestion;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionTag;
import net.starype.quiz.api.util.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionParser {

    private static final ConfigMatcher<PartialEvaluator> EVALUATOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new WorldEvaluatorMapper(),
            new DoubleEvaluatorMapper()
    ), new DefaultEvaluatorMapper());

    private static final ConfigMatcher<AnswerProcessor> PROCESSOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new CleanStringProcessorMapper(),
            new CleanSeparatorProcessorMapper()
    ), new CleanStringProcessorMapper());

    private static final String PROCESSORS = "answer.processors";
    private static final String EVALUATOR = "answer.evaluator";
    private static final String CORRECT = "answer.correct";

    public static Question parse(String filePath) throws IOException {

        CommentedConfig config = loadConfig(filePath);

        String rawText = config.get("question.text");
        Set<QuestionTag> tags = StringUtils.map(config.get("tags"), QuestionTag::new);
        AnswerProcessor processor = loadProcessor(config);
        AnswerEvaluator evaluator = loadEvaluator(config, processor);

        Question question = new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawAnswer(rawText)
                .withTags(tags)
                .build();

        return question;
    }

    private static CommentedConfig loadConfig(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();
        return result;
    }

    private static AnswerProcessor loadProcessor(CommentedConfig result) {
        return result.<List<String>>get(PROCESSORS)
                .stream()
                .map(name -> PROCESSOR_MATCHER.load(name, result))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(AnswerProcessor::combine)
                .orElse(IdentityProcessor.INSTANCE);
    }

    private static AnswerEvaluator loadEvaluator(CommentedConfig result, AnswerProcessor processor) {
        return EVALUATOR_MATCHER
                .checkAll(EVALUATOR, result)
                .create(asAnswers(result), processor);
    }

    private static Set<Answer> asAnswers(CommentedConfig result) {
        return StringUtils.map(result.get(CORRECT), Answer::fromString);
    }
}
