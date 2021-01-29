package net.starype.quiz.api.database;

import net.starype.quiz.api.util.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDatabases {

    public static SimpleQuestionDatabase fromLocalPath(String configPath, String dbPath, boolean standAlone, boolean compressed) {
        SerializedIO serializedIO = (compressed) ? (new CompressedFileSerializedIO(dbPath)) : (new FileSerializedIO(dbPath));
        return fromLocalPath(configPath, serializedIO, standAlone);
    }

    public static SimpleQuestionDatabase fromLocalPath(String configPath, SerializedIO serializedIO, boolean standAlone) {
        File configAsFile = new File(configPath);
        List<String> paths = (configAsFile.isFile()
                ? Collections.singletonList(configAsFile)
                : FileUtils.recursiveListAllFiles(configAsFile))
                .stream()
                .map(File::getPath)
                .collect(Collectors.toList());
        return fromLocalPath(paths, configPath, serializedIO, standAlone);
    }

    public static SimpleQuestionDatabase fromLocalPath(List<String> configPaths, String relativePath,
                                                 SerializedIO serializedIO, boolean standAlone) {
        FilePathReader filePathReader = new SimpleFilePathReader(configPaths, relativePath);
        List<? extends EntryUpdater> updaters = configPaths
                .stream()
                .map(path -> createUpdater(FileUtils.getRelativePath(path, relativePath), filePathReader))
                .collect(Collectors.toList());

        return new SimpleQuestionDatabase(updaters, serializedIO, standAlone);
    }

    private static EntryUpdater createUpdater(String path, FilePathReader filePathReader) {
        return new FileEntryUpdater(path, filePathReader);
    }
}
