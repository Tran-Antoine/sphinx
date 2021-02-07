package net.starype.quiz.api.database;

import net.starype.quiz.api.util.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleFilePathReader implements FilePathReader {

    private final Map<String, ? extends String> relativeToAbsolute;

    public SimpleFilePathReader(Collection<? extends String> trackedFile, String relativePath) {
        relativeToAbsolute = trackedFile
                .stream()
                .collect(Collectors.toMap(k -> FileUtils.getRelativePath(k, relativePath), k -> k));
    }

    @Override
    public Optional<String> read(String path) {
        Optional<String> absolutePath = Optional.ofNullable(relativeToAbsolute.getOrDefault(path, null));
        if(absolutePath.isEmpty())
            return Optional.empty();
        try {
            FileInputStream fis = new FileInputStream(absolutePath.get());
            return Optional.of(new String(fis.readAllBytes()).replace("\r\n", "\n"));
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }
}
