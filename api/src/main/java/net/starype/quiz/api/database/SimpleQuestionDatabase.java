package net.starype.quiz.api.database;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.QuestionParser;
import net.starype.quiz.api.util.FileUtils;
import net.starype.quiz.api.util.RandomComparator;
import net.starype.quiz.api.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleQuestionDatabase extends TrackedDatabase implements QuestionDatabase {

    public final static DatabaseTable TABLE = new DatabaseTable.Builder()
                    .registerIndexedArguments("text")
                    .registerIndexedArguments("difficulty")
                    .registerIndexedArguments("tags")
                    .registerArgument("processors")
                    .registerArgument("answers")
                    .registerArgument("evaluator")
                    .registerArgument("processors")
                    .create();

    public static SimpleQuestionDatabase createDatabaseFromFileList(Set<? extends String> trackedFiles, String relativeDirectory,
                                                                    String databaseFile, boolean standalone, boolean compressed)
    {
        // Create the serializer object
        SerializedIO serializedIO = (compressed)
                ? (new CompressedFileSerializedIO(databaseFile))
                : (new FileSerializedIO(databaseFile));

        // Create the file parser
        Map<String, String> absoluteToRelativeMap = trackedFiles.stream()
                .collect(Collectors.toMap(f -> f, file -> FileUtils.getRelativePath(file, relativeDirectory)));
        Map<String, String> relativeToAbsoluteMap = trackedFiles.stream()
                .collect(Collectors.toMap(file -> FileUtils.getRelativePath(file, relativeDirectory), f -> f));

        FileParser fileParser = QuestionParser.getFileParser(TABLE,
                new SimpleFileInput(file -> trackedFiles.contains(relativeToAbsoluteMap.getOrDefault(file, "")) ?
                        Optional.of(relativeToAbsoluteMap.getOrDefault(file, "")) :
                        Optional.empty()));

        return new SimpleQuestionDatabase(trackedFiles.stream().map(absoluteToRelativeMap::get).collect(Collectors.toSet()),
                serializedIO,
                fileParser,
                standalone);
    }

    public static SimpleQuestionDatabase createDatabaseFromDirectory(String directory, String databaseFile,
                                                              boolean standalone, boolean compressed) {
        // Create the file parser
        Set<? extends String> trackedFiles = FileUtils.recursiveListAllFiles(new File(directory))
                .stream()
                .map(File::getPath)
                .collect(Collectors.toSet());

        return createDatabaseFromFileList(trackedFiles, directory, databaseFile, standalone, compressed);
    }

    private SimpleQuestionDatabase(Set<? extends String> trackedFiles, SerializedIO serializedIO, FileParser fileParser,
                                  boolean standalone) {
        super(TABLE, serializedIO, trackedFiles, fileParser, standalone);
    }

    private boolean getQuery(QuestionQuery queryMatcher, Map<String, String> map) {
        return queryMatcher.apply(new QuestionQuery.QueryData(new HashSet<>(StringUtils.unpack(map.get("tags"))),
                map.get("text"), map.get("difficulty"), map.get("file")));
    }

    @Override
    public List<Question> randomizedQuery(QuestionQuery queryMatcher, int maxCount) {
        return query(argumentValues -> getQuery(queryMatcher, argumentValues))
                .stream()
                .sorted(new RandomComparator<>())
                .map(s -> QuestionParser.parseRaw(
                        s.get("difficulty").orElse("NORMAL"),
                        s.get("tags").orElse(""),
                        s.get("text").orElse(""),
                        s.get("answers").orElse(""),
                        s.get("evaluator").orElse(""),
                        s.get("processors").orElse("")
                ))
                .limit(maxCount)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> listQuery(QuestionQuery queryMatcher) {
        return randomizedQuery(queryMatcher, Integer.MAX_VALUE);
    }

    @Override
    public Optional<Question> pickQuery(QuestionQuery queryMatched) {
        return randomizedQuery(queryMatched,1).stream().findAny();
    }
}
