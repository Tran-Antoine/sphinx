package net.starype.quiz.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<File> listAllFiles(File directory) {
        File[] files = directory.listFiles();
        return (files == null) ? new ArrayList<>() : Arrays.stream(files).filter(File::isFile).collect(Collectors.toList());
    }

    public static List<File> listAllDirectory(File directory) {
        File[] directories = directory.listFiles();
        return (directories == null) ? new ArrayList<>() : Arrays.stream(directories).filter(File::isDirectory).collect(Collectors.toList());
    }

    public static List<File> recursiveListAllFiles(File directory) {
        File[] files = directory.listFiles();
        List<File> fileList = (files == null) ? new ArrayList<>() : Arrays.stream(files).filter(File::isFile).collect(Collectors.toList());
        for(File file : files) {
            if(file.isDirectory()) {
                fileList.addAll(recursiveListAllFiles(file));
            }
        }
        return fileList;
    }

    public static String getRelativePath(String file, String relativeTo)  {
        Path sourceFile = Paths.get(relativeTo);
        Path targetFile = Paths.get(file);
        Path relativePath = sourceFile.relativize(targetFile);
        return relativePath.toString().replace(File.separator, "/");
    }
}
