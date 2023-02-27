package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.starype.quiz.api.database.ReadableRawMap;

import java.util.Optional;

public class ReadableConfig implements ReadableRawMap {

    private final CommentedConfig config;

    private ReadableConfig(CommentedConfig config) {
        this.config = config;
    }

    public static ReadableRawMap of(CommentedConfig config) {
        return new ReadableConfig(config);
    }

    @Override
    public Optional<String> get(String key) {
        return config.getOptional(key);
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return config.getOptional(key);
    }

    @Override
    public Optional<Float> getFloat(String key) {
        return config.getOptional(key);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return config.getOptional(key);
    }
}
