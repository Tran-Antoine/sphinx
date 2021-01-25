package net.starype.quiz.api.parser;

import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.util.FileUtils;
import net.starype.quiz.api.util.RandomComparator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuestionDatabase {

    private TrackedDatabase database;
    private DBTable table;

    public QuestionDatabase(String trackedDirectory, String databaseFile) {
        this(trackedDirectory, databaseFile, false, true);
    }

    public QuestionDatabase(String trackedDirectory, String databaseFile, boolean standalone, boolean compressed) {
        List<String> trackedFiles = standalone ? new ArrayList<>() :
                FileUtils.recursiveListAllFiles(new File(trackedDirectory))
                        .stream()
                        .map(File::getPath)
                        .collect(Collectors.toList());
        setup(trackedFiles, databaseFile, standalone, compressed);
    }

    private void setup(List<String> trackedFiles, String databaseFile, boolean standalone, boolean compressed) {
        // First initialize the table
        table = new DBTable.Builder()
                .registerIndexedArguments("text")
                .registerIndexedArguments("difficulty")
                .registerIndexedArguments("tags")
                .registerArgument("processors")
                .registerArgument("answers")
                .registerArgument("evaluator")
                .registerArgument("processors")
                .create();

        // Create the serializable object
        SerializedIO serializer = new FileSerializeIO(databaseFile, compressed);

        // Create the file parser
        FileParser fileParser = QuestionParser.getFileParser(file -> trackedFiles.contains(file) ?
                (Optional.of(file)) : (Optional.empty()), table);

        // Input & Output
        database = new TrackedDatabase.Builder()
                .setTable(table)
                .setTrackedFiles(trackedFiles)
                .setIO(serializer)
                .setParser(fileParser)
                .setStandalone(standalone)
                .create();
    }

    public DBTable getTable() {
        return table;
    }

    public void sync() {
        database.sync();
    }

    public List<Question> randomizedQuery(IndexQuery queryMatcher, int maxCount) {
        return database.query(queryMatcher).stream()
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

    public List<Question> queryAll(IndexQuery queryMatcher) {
        return randomizedQuery(queryMatcher, Integer.MAX_VALUE);
    }

    public Optional<Question> pickQuery(IndexQuery queryMatched) {
        return randomizedQuery(queryMatched,1).stream().findAny();
    }
}
