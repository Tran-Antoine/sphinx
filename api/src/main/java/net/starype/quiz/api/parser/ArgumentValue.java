package net.starype.quiz.api.parser;

public class ArgumentValue implements Argument {
    private final String name;
    private final String value;

    public ArgumentValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
