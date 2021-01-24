package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.FileUtils;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionDatabase {

    private TrackedDatabase db;
    private DBTable table;

    public QuestionDatabase(String trackedDirectory, String databaseFile) {
        this(trackedDirectory, databaseFile, false);
    }

    public QuestionDatabase(String trackedDirectory, String databaseFile, boolean standalone) {
        setup(FileUtils.recursiveListAllFiles(new File(trackedDirectory)).stream().map(File::getPath).collect(Collectors.toSet()),
                databaseFile, standalone);
    }

    private void setup(Set<String> collect, String databaseFile, boolean standalone) {
        // First initialize the table
        table = new DBTable.Builder()
                .registerIndexedArguments("question-name")
                .registerIndexedArguments("difficulty")
                .registerIndexedArguments("processors")
                .registerIndexedArguments("")
                .create();
    }
}
