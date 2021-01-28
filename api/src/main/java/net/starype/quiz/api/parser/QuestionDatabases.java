package net.starype.quiz.api.parser;

import net.starype.quiz.api.parser.TrackedDatabase.UpdatableEntry;
import net.starype.quiz.api.util.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDatabases {

    public static QuestionDatabase fromLocalPath(String configPath, String dbPath, DBTable table, boolean standAlone, boolean compressed) {
        SerializedIO serializedIO = new FileSerializedIO(dbPath, compressed);
        return fromLocalPath(configPath, serializedIO, table, standAlone);
    }

    public static QuestionDatabase fromLocalPath(String configPath, SerializedIO serializedIO, DBTable table, boolean standAlone) {
        File configAsFile = new File(configPath);
        List<String> paths = (configAsFile.isFile()
                ? Collections.singletonList(configAsFile)
                : FileUtils.recursiveListAllFiles(configAsFile))
                .stream()
                .map(File::getPath)
                .collect(Collectors.toList());
        return fromLocalPath(paths, serializedIO, table, standAlone);
    }

    public static QuestionDatabase fromLocalPath(List<String> configPaths, SerializedIO serializedIO, DBTable table, boolean standAlone) {
        List<? extends UpdatableEntry> updaters = configPaths
                .stream()
                .map(path -> createUpdater(path, table))
                .collect(Collectors.toList());

        return new SimpleQuestionDatabase(updaters, serializedIO, standAlone);
    }

    private static UpdatableEntry createUpdater(String path, DBTable table) {
        FilePathReader filePathReader = new SimpleFilePathReader(path);
        return new FileEntryUpdater(path, table, filePathReader);
    }
}
