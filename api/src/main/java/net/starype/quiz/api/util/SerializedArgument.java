package net.starype.quiz.api.util;

import net.starype.quiz.api.parser.Argument;

import java.util.Optional;

public class SerializedArgument implements Argument {
    private final int size;
    private final String name;

    public SerializedArgument(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public SerializedArgument(String name) {
        this(name, 0);
    }

    public Optional<Integer> getSize() {
        return (size > 0) ?
                Optional.of(size) : Optional.empty();
    }

    @Override
    public String getName() {
        return name;
    }
}
