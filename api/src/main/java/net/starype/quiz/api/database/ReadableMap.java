package net.starype.quiz.api.database;

import java.util.Optional;

public interface ReadableMap {
    Optional<String> get(String value);
}
