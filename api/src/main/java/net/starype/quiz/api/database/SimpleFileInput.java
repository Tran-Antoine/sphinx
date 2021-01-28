package net.starype.quiz.api.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class SimpleFileInput implements FileInput {

    private final Function<String, Optional<String>> filePath;

    public SimpleFileInput(Function<String, Optional<String>> filePath) {
        this.filePath = filePath;
    }

    @Override
    public Optional<String> read(String file) {
        try {
            Optional<String> optionalFile = filePath.apply(file);
            if(optionalFile.isEmpty()) return Optional.empty();

            FileInputStream fis = new FileInputStream(optionalFile.get());
            return Optional.of(new String(fis.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
