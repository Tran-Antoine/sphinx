package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlParser;
import net.starype.quiz.api.database.*;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.answer.AnswerEvaluator;
import net.starype.quiz.api.game.answer.AnswerProcessor;
import net.starype.quiz.api.game.answer.IdentityProcessor;
import net.starype.quiz.api.game.question.DefaultQuestion;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.game.question.QuestionDifficulty;
import net.starype.quiz.api.game.question.QuestionTag;
import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.CollectionUtils;
import net.starype.quiz.api.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

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
    ), new DifficultyMapper("default", QuestionDifficulty.NORMAL));

    private static final String PROCESSORS = "answer.processors";
    private static final String EVALUATOR = "answer.evaluator";
    private static final String EVALUATOR_NAME = "answer.evaluator.name";
    private static final String CORRECT = "answer.correct";
    private static final String DIFFICULTY = "difficulty";

    public static Question parseRaw(String rawDifficulty, String inlineTags, String rawText, String inlineAnswers,
                                    String inlineEvaluators, String inlineProcessors) {
        List<String> rawAnswers = StringUtils.unpack(inlineAnswers);
        Map<String, String> rawEvaluators = StringUtils.unpackMap(inlineEvaluators);
        Map<String, String> rawProcessors = StringUtils.unpackMap(inlineProcessors);

        Set<QuestionTag> tags = new HashSet<>(StringUtils.unpack(inlineTags, QuestionTag::new));
        AnswerProcessor processor = loadProcessor(arg -> Optional.ofNullable(rawProcessors.get(arg)));
        AnswerEvaluator evaluator = loadEvaluator(arg -> Optional.ofNullable(rawEvaluators.get(arg)),
                processor, rawAnswers);
        QuestionDifficulty difficulty = loadDifficulty(arg -> Optional.of(rawDifficulty));

        return new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawText(rawText)
                .withRawAnswer(String.join(",", rawAnswers))
                .withTags(tags)
                .withDifficulty(difficulty)
                .build();
    }

    private static Set<String> getKeysBySubPath(String subPath, Set<? extends CommentedConfig.Entry> config) {
        return config.stream()
                .map(s -> (s.getValue() instanceof CommentedConfig) ?
                        getKeysBySubPath(StringUtils.concatWithSeparator(subPath, s.getKey(), "."),
                                s.<CommentedConfig>getValue().entrySet()) :
                        Set.of(StringUtils.concatWithSeparator(subPath, s.getKey(), ".")))
                .reduce(CollectionUtils::concat).orElse(new HashSet<>());
    }

    public static Set<DatabaseEntry> getDatabaseEntries(String file, FilePathReader fileInput, DatabaseEntryFactory databaseEntryFactory) {
        Optional<String> parsedFile = fileInput.read(file);
        if (parsedFile.isEmpty())
            return new HashSet<>();

        CommentedConfig config = loadConfigFromString(parsedFile.get());
        Set<String> inlineEntriesSet = getKeysBySubPath("", config.entrySet());
        Map<String, String> argMap = inlineEntriesSet.stream()
                .collect(Collectors.toMap(path -> path, path -> (config.get(path) instanceof List<?>) ?
                        StringUtils.pack(config.get(path)) : config.get(path)));

        String rawText = config.get("question.text");
        String inlineTags = StringUtils.pack(config.get("tags"));
        String inlineAnswers = StringUtils.pack(config.get(CORRECT));
        String inlineProcessors = StringUtils.packMap(argMap.keySet()
                .stream()
                .filter(key -> key.startsWith(PROCESSORS))
                .collect(Collectors.toMap(k -> k, argMap::get)));
        String inlineEvaluator = StringUtils.packMap(argMap.keySet()
                .stream()
                .filter(key -> key.startsWith(EVALUATOR))
                .collect(Collectors.toMap(k -> k, argMap::get)));
        String rawDifficulty = config.get(DIFFICULTY);

        DatabaseEntry entry = databaseEntryFactory.generateNewEntry();
        entry.set("text", rawText);
        entry.set("difficulty", rawDifficulty);
        entry.set("tags", inlineTags);
        entry.set("processors", inlineProcessors);
        entry.set("answers", inlineAnswers);
        entry.set("evaluator", inlineEvaluator);
        entry.set("processors", inlineProcessors);
        return Set.of(entry);
    }

    public static Question parseTOML(String filePath) throws IOException {

        CommentedConfig config = loadConfigFromFile(filePath);

        String rawText = config.get("question.text");
        Set<QuestionTag> tags = StringUtils.map(config.get("tags"), QuestionTag::new);
        List<String> rawAnswers = config.get(CORRECT);
        AnswerProcessor processor = loadProcessor(config::getOptional);
        AnswerEvaluator evaluator = loadEvaluator(config::getOptional, processor, rawAnswers);
        QuestionDifficulty difficulty = loadDifficulty(config::getOptional);

        return new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawText(rawText)
                .withRawAnswer(String.join(", ", rawAnswers))
                .withTags(tags)
                .withDifficulty(difficulty)
                .build();
    }

    private static QuestionDifficulty loadDifficulty(ReadableMap config) {
        return DIFFICULTY_MATCHER.loadFromKeyOrDefault(DIFFICULTY, config);
    }

    private static CommentedConfig loadConfigFromFile(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();
        return result;
    }

    private static CommentedConfig loadConfigFromString(String str) {
        return new TomlParser().parse(str);
    }

    private static AnswerProcessor loadProcessor(ReadableMap config) {
        return PROCESSOR_MATCHER.loadList(PROCESSORS, config)
                .stream()
                .reduce(AnswerProcessor::combine)
                .orElse(IdentityProcessor.INSTANCE);
    }

    private static AnswerEvaluator loadEvaluator(ReadableMap config, AnswerProcessor processor, List<String> rawAnswers) {
        return EVALUATOR_MATCHER
                .loadFromKeyOrDefault(EVALUATOR_NAME, config)
                .create(StringUtils.map(rawAnswers, Answer::fromString), processor);
    }
}
