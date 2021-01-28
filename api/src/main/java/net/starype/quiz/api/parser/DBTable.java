package net.starype.quiz.api.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * An {@link DBTable} is an object that contains a list of argument and a sublist of indexedArguments <br>
 * It is used to defines an generic entry for any DataBase
 */
public class DBTable {
    private final Set<? extends String> arguments;
    private final Set<? extends String> indexedArguments;

    private DBTable(Set<? extends String> arguments, Set<? extends String> indexedArguments) {
        this.arguments = arguments;
        this.indexedArguments = indexedArguments;
    }

    /**
     * Get the list of all the arguments of the table
     * @return {@link Set} of {@link String}s representing the argument of the DB
     */
    public Set<? extends String> getArguments() {
        return arguments;
    }

    /**
     * Get the list of all the indexed arguments of the table. This list is necessary as a subset of the arguments
     * list.
     * @return {@link Set} of {@link String}s that hold all the arguments
     */
    public Set<? extends String> getIndexedArguments() {
        return indexedArguments;
    }

    /**
     * Check if the table contains a given argument
     * @param argument {@link String} that hold the argument we are checking the existence
     * @return An {@link Boolean} that hold whether or not the given argument is contained in the table
     */
    public boolean containsArgument(String argument) {
        return arguments.stream().anyMatch(arg -> arg.equals(argument));
    }

    public static class Builder {
        private final Set<String> arguments;
        private final Set<String> indexedArguments;

        /**
         * Default {@link DBTable.Builder} constructor
         */
        public Builder() {
            arguments = new HashSet<>();
            indexedArguments = new HashSet<>();
        }

        /**
         * Register a new argument to the constructed table
         * @param argument the new argument
         * @return itself for chaining purposes
         */
        public Builder registerArgument(String argument) {
            arguments.add(argument);
            return this;
        }

        /**
         * Register a new indexed argument to the constructed table
         * @param argument the new indexed arguments
         * @return itself for chaining purposes
         */
        public Builder registerIndexedArguments(String argument) {
            arguments.add(argument);
            indexedArguments.add(argument);
            return this;
        }

        /**
         * Create a new instance of {@link DBTable}
         * @return the {@link DBTable} created from the configuration
         */
        public DBTable create() {
            return new DBTable(new HashSet<>(arguments), new HashSet<>(indexedArguments));
        }
    }
}
