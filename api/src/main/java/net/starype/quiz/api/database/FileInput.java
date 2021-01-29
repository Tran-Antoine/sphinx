package net.starype.quiz.api.database;

import java.util.Optional;

public interface FileInput {
    Optional<String> read(String file);
}
