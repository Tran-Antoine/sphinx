package net.starype.quiz.api.parser;

import net.starype.quiz.api.database.ReadableMap;

/**
 * Maps a key name with an object generating procedure. <br>
 * This allows the creation of more sophisticated objects from TOML values. {@link ConfigMapper#getMapperName()} represents the value
 * that a given key must be associated with in order for the mapper to take effect.
 * @param <R> the type of produced result
 */
public interface ConfigMapper<R> {

    /**
     * @return the value that may match a key from a TOML configuration file
     */
    String getMapperName();

    /**
     * Produce a result of a specific type. <br>
     * The configuration object may be used to gather additional information required to construct the object.
     * @param config the configuration object loaded from the file
     * @return a result representing the value
     */
    R map(ReadableMap config);
}
