package net.starype.quiz.api.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class SimpleFilePathReader implements FilePathReader {

    private final String filePath;

    public SimpleFilePathReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Optional<String> read(String path) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            return Optional.of(new String(fis.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
