package net.starype.quiz.api.parser;

import java.util.Optional;

public interface FileInput {
    Optional<String> read(String file);
}
