package net.starype.quiz.api.parser;

import java.util.Optional;

public interface ReadableMap {
    Optional<String> get(String value);
}
