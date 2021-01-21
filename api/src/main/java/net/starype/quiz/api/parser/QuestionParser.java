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
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.game.question.QuestionTag;
import net.starype.quiz.api.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Util class that allows toml configuration file to {@link Question} conversions
 */
public class QuestionParser {

    private static final ConfigMatcher<PartialEvaluator> EVALUATOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new WordEvaluatorMapper(),
            new DoubleEvaluatorMapper(),
            new IntegerEvaluatorMapper(),
            new MCQEvaluatorMapper()
    ), new ExactEvaluatorMapper());

    private static final ConfigMatcher<AnswerProcessor> PROCESSOR_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new CleanStringProcessorMapper(),
            new CleanSeparatorProcessorMapper(),
            new TrueFalseProcessorMapper()
    ), new CleanStringProcessorMapper());

    private static final ConfigMatcher<QuestionDifficulty> DIFFICULTY_MATCHER = new ConfigMatcher<>(Arrays.asList(
            new DifficultyMapper("easy", QuestionDifficulty.EASY),
            new DifficultyMapper("normal", QuestionDifficulty.NORMAL),
            new DifficultyMapper("hard", QuestionDifficulty.HARD),
            new DifficultyMapper("insane", QuestionDifficulty.INSANE)
    ), new DifficultyMapper("default", QuestionDifficulty.EASY));

    private static final String PROCESSORS = "answer.processors";
    private static final String EVALUATOR = "answer.evaluator.name";
    private static final String CORRECT = "answer.correct";
    private static final String DIFFICULTY = "difficulty";

    /**
     * Parse a given TOML configuration file and converts it into a Question object
     * @param filePath the full path of the file
     * @return a Question object
     * @throws IOException if the file was not found
     */
    public static Question parseTOML(String filePath) throws IOException {

        CommentedConfig config = loadConfig(filePath);

        String rawText = config.get("question.text");
        Set<QuestionTag> tags = StringUtils.map(config.get("tags"), QuestionTag::new);
        List<String> rawAnswers = config.get(CORRECT);
        AnswerProcessor processor = loadProcessor(config);
        AnswerEvaluator evaluator = loadEvaluator(config, processor, rawAnswers);
        QuestionDifficulty difficulty = loadDifficulty(config);

        Question question = new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawText(rawText)
                .withRawAnswer(String.join(", ", rawAnswers))
                .withTags(tags)
                .withDifficulty(difficulty)
                .build();

        return question;
    }

    private static QuestionDifficulty loadDifficulty(CommentedConfig config) {
        return DIFFICULTY_MATCHER.loadFromKeyOrDefault(DIFFICULTY, config);
    }

    private static CommentedConfig loadConfig(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();
        return result;
    }

    private static AnswerProcessor loadProcessor(CommentedConfig config) {
        return PROCESSOR_MATCHER.loadList(PROCESSORS, config)
                .stream()
                .reduce(AnswerProcessor::combine)
                .orElse(IdentityProcessor.INSTANCE);
    }

    private static AnswerEvaluator loadEvaluator(CommentedConfig config, AnswerProcessor processor, List<String> rawAnswers) {
        return EVALUATOR_MATCHER
                .loadFromKeyOrDefault(EVALUATOR, config)
                .create(StringUtils.map(rawAnswers, Answer::fromString), processor);
    }
}
