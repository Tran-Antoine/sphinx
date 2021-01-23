package net.starype.quiz.api.parser;

public class ArgumentValue implements Argument {
    private final String name;
    private String value;

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

    public void setValue(String value) {
        this.value = value;
    }
}
