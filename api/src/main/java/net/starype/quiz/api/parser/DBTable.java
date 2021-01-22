package net.starype.quiz.api.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBTable {
    private final Set<? extends String> arguments;
    private final Set<? extends String> indexedArguments;

    private DBTable(Set<? extends String> arguments, Set<? extends String> indexedArguments) {
        this.arguments = arguments;
        this.indexedArguments = indexedArguments;
    }

    public Set<? extends String> getArguments() {
        return arguments;
    }

    public Set<? extends String> getIndexedArguments() {
        return indexedArguments;
    }

    public boolean containsArgument(String argument) {
        return arguments.stream().anyMatch(arg -> arg.equals(argument));
    }

    public static class Builder {
        private final Set<String> arguments;
        private final Set<String> indexedArguments;

        public Builder() {
            arguments = new HashSet<>();
            indexedArguments = new HashSet<>();
        }

        public Builder registerArgument(String argument) {
            arguments.add(argument);
            return this;
        }

        public Builder registerIndexedArguments(String argument) {
            arguments.add(argument);
            indexedArguments.add(argument);
            return this;
        }

        public DBTable create() {
            return new DBTable(new HashSet<>(arguments), new HashSet<>(indexedArguments));
        }
    }
}
