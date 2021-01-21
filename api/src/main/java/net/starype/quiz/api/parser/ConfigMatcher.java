package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;

import java.util.Collection;
import java.util.Optional;

public class ConfigMatcher<T> {

    private Collection<? extends ConfigMapper<T>> mappers;
    private ConfigMapper<T> defaultMapper;

    public ConfigMatcher(Collection<ConfigMapper<T>> mappers, ConfigMapper<T> defaultMapper) {
        this.mappers = mappers;
        this.defaultMapper = defaultMapper;
    }

    public Optional<T> load(String name, CommentedConfig config) {
        return mappers
                .stream()
                .filter(mapper -> mapper.getEvaluatorName().equals(name))
                .findAny()
                .map(mapper -> mapper.map(config));
    }

    public T checkAll(String section, CommentedConfig config) {
        return mappers
                .stream()
                .filter(mapper -> config.get(section).equals(mapper.getEvaluatorName()))
                .findAny()
                .map(mapper -> mapper.map(config))
                .orElse(defaultMapper.map(config));
    }
}
