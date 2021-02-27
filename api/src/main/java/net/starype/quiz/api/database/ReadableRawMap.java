package net.starype.quiz.api.database;

import net.starype.quiz.api.util.StringUtils;

import java.awt.geom.Line2D;
import java.util.Optional;

public interface ReadableRawMap {

    Optional<String> get(String key);

    default Optional<Integer> getInt(String key) {
        return StringUtils.mapOptionalNoThrow(get(key), Integer::valueOf);
    }

    default Optional<Double> getDouble(String key) {
        return StringUtils.mapOptionalNoThrow(get(key), Double::valueOf);
    }

    default Optional<Float> getFloat(String key) {
        return StringUtils.mapOptionalNoThrow(get(key), Float::valueOf);
    }

    default String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    default String getOrEmpty(String key) {
        return getOrDefault(key, "");
    }
}
