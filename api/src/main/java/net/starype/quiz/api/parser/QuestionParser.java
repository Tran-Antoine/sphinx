package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.database.*;
import net.starype.quiz.api.answer.Answer;
import net.starype.quiz.api.answer.AnswerEvaluator;
import net.starype.quiz.api.answer.AnswerProcessor;
import net.starype.quiz.api.answer.IdentityProcessor;
import net.starype.quiz.api.question.DefaultQuestion;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.question.QuestionDifficulty;
import net.starype.quiz.api.question.QuestionTag;
import net.starype.quiz.api.util.CollectionUtils;
import net.starype.quiz.api.util.NightConfigParserUtils;
import net.starype.quiz.api.util.StringUtils;

import java.io.IOException;
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

    public static Question parse(ReadableRawMap config) {

        String rawText = config.getOrEmptyString("text");
        List<String> rawAnswers = StringUtils.unpack(config.getOrEmptyString("answers"));
        AnswerProcessor processor = loadProcessor(config);
        AnswerEvaluator evaluator = loadEvaluator(config, processor, rawAnswers);
        Set<QuestionTag> tags = new HashSet<>(StringUtils.unpack(config.getOrEmptyString("tags"), QuestionTag::new));
        QuestionDifficulty difficulty = QuestionDifficulty.valueOf(config.getOrDefault("difficulty", "NORMAL"));

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
                .map(s -> (s.getValue() instanceof CommentedConfig)
                        ? getKeysBySubPath(StringUtils.concatWithSeparator(subPath, s.getKey(), "."),
                                s.<CommentedConfig>getValue().entrySet())
                        : Set.of(StringUtils.concatWithSeparator(subPath, s.getKey(), ".")))
                .reduce(CollectionUtils::concat).orElse(new HashSet<>());
    }

    public static Set<DatabaseEntry> getDatabaseEntries(String file, FilePathReader fileInput, DatabaseEntryFactory databaseEntryFactory) {
        Optional<String> parsedFile = fileInput.read(file);
        if (parsedFile.isEmpty())
            return new HashSet<>();
        return getDatabaseEntries(parsedFile.get(), databaseEntryFactory);
    }

    public static Set<DatabaseEntry> getDatabaseEntries(String content, DatabaseEntryFactory factory) {
        CommentedConfig config = NightConfigParserUtils.loadConfigFromString(content);
        Set<String> inlineEntriesSet = getKeysBySubPath("", config.entrySet());
        Map<String, String> argMap = inlineEntriesSet.stream()
                .collect(Collectors.toMap(path -> path, path -> (config.get(path) instanceof List<?>) ?
                        StringUtils.pack(config.get(path)) : config.get(path)));

        String rawText = config.get("question.text");
        String inlineTags = StringUtils.pack(config.get("tags"));
        String inlineAnswers = StringUtils.pack(config.get(CORRECT));
        String inlineProcessors = StringUtils.pack(config.get("answer.processors"));
        String inlineEvaluator = StringUtils.packMap(argMap.keySet()
                .stream()
                .filter(key -> key.startsWith(EVALUATOR))
                .collect(Collectors.toMap(k -> k, argMap::get)));
        String rawDifficulty = config.get(DIFFICULTY);

        DatabaseEntry entry = factory.generateNewEntry();
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
        CommentedConfig config = NightConfigParserUtils.loadConfigFromFile(filePath);

        String rawText = config.get("question.text");
        Set<QuestionTag> tags = StringUtils.map(config.get("tags"), QuestionTag::new);
        List<String> rawAnswers = config.get(CORRECT);
        AnswerProcessor processor = loadProcessor(config);
        AnswerEvaluator evaluator = loadEvaluator(config, processor, rawAnswers);
        QuestionDifficulty difficulty = loadDifficulty(config);

        return new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawText(rawText)
                .withRawAnswer(String.join(", ", rawAnswers))
                .withTags(tags)
                .withDifficulty(difficulty)
                .build();
    }

    private static QuestionDifficulty loadDifficulty(CommentedConfig config) {
        return DIFFICULTY_MATCHER
                .loadFromKey(DIFFICULTY, ReadableConfig.of(config))
                .orElse(QuestionDifficulty.NORMAL);
    }

    private static AnswerProcessor loadProcessor(CommentedConfig config) {
        return PROCESSOR_MATCHER
                .loadList(PROCESSORS, ReadableConfig.of(config))
                .stream()
                .reduce(AnswerProcessor::combine)
                .orElse(IdentityProcessor.INSTANCE);
    }

    private static AnswerProcessor loadProcessor(ReadableRawMap config) {
        return PROCESSOR_MATCHER
                .map(StringUtils.unpack(config.getOrDefault("processors", "")), null)
                .stream()
                .reduce(AnswerProcessor::combine)
                .orElse(IdentityProcessor.INSTANCE);
    }

    private static AnswerEvaluator loadEvaluator(CommentedConfig config, AnswerProcessor processor, List<String> rawAnswers) {
        return EVALUATOR_MATCHER
                .loadFromKeyOrDefault(EVALUATOR_NAME, ReadableConfig.of(config))
                .create(StringUtils.map(rawAnswers, Answer::fromString), processor);
    }

    private static AnswerEvaluator loadEvaluator(ReadableRawMap config, AnswerProcessor processor, List<String> rawAnswers) {
        Map<String, String> rawData = StringUtils.unpackMap(config.getOrDefault("evaluator", ""));
        return EVALUATOR_MATCHER
                .loadFromValueOrDefault(rawData.get("answer.evaluator.name"), config)
                .create(StringUtils.map(rawAnswers, Answer::fromString), processor);
    }

}
