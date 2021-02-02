package net.starype.quiz.api.database;

import java.util.Optional;

public interface ReadableRawMap {

    Optional<String> get(String key);

    default String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    default String getOrEmpty(String key) {
        return getOrDefault(key, "");
    }
}
