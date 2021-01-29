package net.starype.quiz.api.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class SimpleFilePathReader implements FilePathReader {

    private final Function<String, Optional<String>> filePath;

    public SimpleFilePathReader(Function<String, Optional<String>> filePath) {
        this.filePath = filePath;
    }

    @Override
    public Optional<String> read(String path) {
        try {
            Optional<String> optionalFile = filePath.apply(path);
            if(optionalFile.isEmpty()) return Optional.empty();

            FileInputStream fis = new FileInputStream(optionalFile.get());
            return Optional.of(new String(fis.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
