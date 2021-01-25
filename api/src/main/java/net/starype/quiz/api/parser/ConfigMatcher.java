package net.starype.quiz.api.parser;

import com.electronwill.nightconfig.core.CommentedConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Groups {@link ConfigMapper}s, allowing simple matchings by key or value.
 * @param <T> the type of result produced by the mappers
 */
public class ConfigMatcher<T> {

    private Collection<? extends ConfigMapper<T>> mappers;
    private ConfigMapper<T> defaultMapper;

    /**
     * Constructs a ConfigMatcher with a default mapper, used in case {@link #loadFromKeyOrDefault(String, ReadableMap)} fails
     * to find any mapper.
     * @param mappers the collection of mappers used for matchings
     * @param defaultMapper a default value when matching fails (may be null)
     */
    public ConfigMatcher(Collection<ConfigMapper<T>> mappers, ConfigMapper<T> defaultMapper) {
        this.mappers = mappers;
        this.defaultMapper = defaultMapper;
    }

    /**
     * Produce a result from the first mapper whose name equals (case ignored) the value associated to the given key, then store it
     * into an optional. If no mapper matches the given key, an empty optional is returned
     * @param key the full path to the key
     * @param config the config loaded from the TOML file
     * @return an optional containing the result created, if present
     */
    public Optional<T> loadFromKey(String key, ReadableMap config) {
        return loadFromValue(config.get(key).orElse(null), config);
    }

    /**
     * Produce a result from the first mapper whose name equals (case ignored) the given value, then store it into an optional
     * @param value the given value
     * @param config the config loaded from the TOML file
     * @return an optional containing the result created, if present
     */
    public Optional<T> loadFromValue(String value, ReadableMap config) {
        return mappers
                .stream()
                .filter(mapper -> mapper.getMapperName().equalsIgnoreCase(value))
                .findAny()
                .map(mapper -> mapper.map(config));
    }

    /**
     * Produce a list of results from the list of values associated to the given key. The algorithm tries to match every value
     * found to a mapper stored, and adds the result created if it succeeds to do so.
     * @param key the full path to the key corresponding to a list of string values
     * @param config the config loaded from the TOML file
     * @return a potentially empty collection containing all the results successfully computed
     */
    public Collection<T> loadList(String key, ReadableMap config) {
        return config.<List<String>>get(key)
                .stream()
                .map(name -> loadFromValue(name, config))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Produce a result from the first mapper whose name equals (case ignored) the value associated to the given key
     * @param key the full path to the key
     * @param config the config loaded from the TOML file
     * @return the result created if present, otherwise the default value defined when constructing the object
     */
    public T loadFromKeyOrDefault(String key, ReadableMap config) {
        return loadFromKey(key, config).orElse(defaultMapper.map(config));
    }
}
