package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigMatcher<T> {

    private Collection<? extends ConfigMapper<T>> mappers;
    private ConfigMapper<T> defaultMapper;

    public ConfigMatcher(Collection<ConfigMapper<T>> mappers, ConfigMapper<T> defaultMapper) {
        this.mappers = mappers;
        this.defaultMapper = defaultMapper;
    }

    public Optional<T> load(String sectionName, CommentedConfig config) {
        return loadFromValue(config.get(sectionName), config);
    }

    private Optional<T> loadFromValue(String name, CommentedConfig config) {
        return mappers
                .stream()
                .filter(mapper -> mapper.getMapperName().equalsIgnoreCase(name))
                .findAny()
                .map(mapper -> mapper.map(config));
    }

    public Collection<T> loadList(String sectionName, CommentedConfig config) {
        return config.<List<String>>get(sectionName)
                .stream()
                .map(name -> loadFromValue(name, config))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public T loadOrDefault(String sectionName, CommentedConfig config) {
        return load(sectionName, config).orElse(defaultMapper.map(config));
    }

    public T checkAll(String section, CommentedConfig config) {
        String prefix = section.isEmpty() ? "" : section + ".";
        return mappers
                .stream()
                .filter(mapper -> config.<String>get(prefix + "name").equalsIgnoreCase(mapper.getMapperName()))
                .findAny()
                .map(mapper -> mapper.map(config))
                .orElse(defaultMapper.map(config));
    }
}
