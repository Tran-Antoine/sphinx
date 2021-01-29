package net.starype.quiz.api.database;

import java.util.Optional;

public interface FilePathReader {
    Optional<String> read(String path);
}
