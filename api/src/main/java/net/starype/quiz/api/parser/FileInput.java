package net.starype.quiz.api.parser;

import java.util.Optional;

public interface FileIO {
    Optional<String> read(String file);
}
