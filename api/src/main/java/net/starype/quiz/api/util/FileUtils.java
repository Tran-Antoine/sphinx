package net.starype.quiz.api.util;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<File> listAllFiles(File directory) {
        File[] files = directory.listFiles();
        if(files == null) {
            return Collections.emptyList();
        }
        return Arrays
                .stream(files)
                .filter(File::isFile)
                .collect(Collectors.toList());
    }

    public static List<File> listAllDirectories(File directory) {
        File[] directories = directory.listFiles();
        if(directories == null) {
            return Collections.emptyList();
        }
        return Arrays
                .stream(directories)
                .filter(File::isDirectory)
                .collect(Collectors.toList());
    }

    public static List<File> recursiveListAllFiles(File directory) {

        File[] files = directory.listFiles();
        List<File> fileList = new ArrayList<>();

        for(File file : Objects.requireNonNull(files)) {
            if(file.isFile()) {
                fileList.add(file);
                continue;
            }
            fileList.addAll(recursiveListAllFiles(file));
        }
        return fileList;
    }
}
