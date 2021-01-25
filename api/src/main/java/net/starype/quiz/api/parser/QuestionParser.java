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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        AnswerProcessor processor = loadProcessor(arg -> Optional.ofNullable(rawProcessors.getOrDefault(arg, null)));
        AnswerEvaluator evaluator = loadEvaluator(arg -> Optional.ofNullable(rawEvaluators.getOrDefault(arg, null)),
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
                        getKeysBySubPath(StringUtils.concatWithSeparator(subPath, s.getKey(),"."),
                                s.<CommentedConfig>getValue().entrySet()) :
                        Set.of(StringUtils.concatWithSeparator(subPath, s.getKey(), ".")))
                .reduce((s1,s2) -> Stream.concat(s1.stream(), s2.stream()).collect(Collectors.toSet())).orElse(new HashSet<>());
    }

    public static FileParser getFileParser(Function<String, Optional<String>> filePath, DBTable table) {
        return file -> {
            try {
                CommentedConfig config = loadConfig(filePath.apply(file).orElseThrow());
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
                DBEntry entry = new DBEntry(table);
                entry.set("text", rawText);
                entry.set("difficulty", rawDifficulty);
                entry.set("tags", inlineTags);
                entry.set("processors", inlineProcessors);
                entry.set("answers", inlineAnswers);
                entry.set("evaluator", inlineEvaluator);
                entry.set("processors", inlineProcessors);
                return Set.of(entry);
            } catch (IOException | NoSuchElementException e) {
                return new HashSet<>();
            }
        };
    }

    public static Question parseTOML(String filePath) throws IOException {

        CommentedConfig config = loadConfig(filePath);

        String rawText = config.get("question.text");
        Set<QuestionTag> tags = StringUtils.map(config.get("tags"), QuestionTag::new);
        List<String> rawAnswers = config.get(CORRECT);
        AnswerProcessor processor = loadProcessor(config::getOptional);
        AnswerEvaluator evaluator = loadEvaluator(config::getOptional, processor, rawAnswers);
        QuestionDifficulty difficulty = loadDifficulty(config::getOptional);

        Question question = new DefaultQuestion.Builder()
                .withAnswerEvaluator(evaluator)
                .withRawText(rawText)
                .withRawAnswer(String.join(", ", rawAnswers))
                .withTags(tags)
                .withDifficulty(difficulty)
                .build();

        return question;
    }

    private static QuestionDifficulty loadDifficulty(ReadableMap config) {
        return DIFFICULTY_MATCHER.loadFromKeyOrDefault(DIFFICULTY, config);
    }

    private static CommentedConfig loadConfig(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();
        return result;
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
