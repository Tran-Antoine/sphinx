package net.starype.quiz.api.parser;

import java.util.Collection;

/**
 * Defines an argument based on a name and a value
 * @param <T> The type of the value associate to the name
 */
public class ArgumentValue<T> implements Argument {
    private final String name;
    private T value;

    /**
     * Default constructor of the ArgumentValue
     * @param name the name of the argument associate to the given value
     * @param value generic value that is associate to the given name
     */
    public ArgumentValue(String name, T value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Retrieve the name of the argument of current instance of {@link ArgumentValue}
     * @return The name of the argument
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retrieve the value associate to the argument
     * @return T the value stored in the current {@link ArgumentValue} instance
     */
    public T getValue() {
        return value;
    }

    /**
     * Set the value stored by the {@link ArgumentValue} to a given value
     * @param value the new value stored by the {@link ArgumentValue} instance
     */
    public void setValue(T value) {
        this.value = value;
    }
}
