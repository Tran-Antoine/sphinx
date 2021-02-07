package net.starype.quiz.api.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An {@link DatabaseTable} is an object that contains a list of argument and a sublist of indexedArguments <br>
 * It is used to defines an generic entry for any DataBase
 */
public class DatabaseTable {

    private final List<? extends String> arguments;
    private final List<? extends String> indexedArguments;

    private DatabaseTable(List<? extends String> arguments, List<? extends String> indexedArguments) {
        this.arguments = arguments;
        this.indexedArguments = indexedArguments;
    }

    /**
     * Get the list of all the arguments of the table
     * @return {@link Set} of {@link String}s representing the argument of the DB
     */
    public List<? extends String> getArguments() {
        return arguments;
    }

    /**
     * Get the list of all the indexed arguments of the table. This list is necessary as a subset of the arguments
     * list.
     * @return {@link Set} of {@link String}s that hold all the arguments
     */
    public List<? extends String> getIndexedArguments() {
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
        private final List<String> arguments;
        private final List<String> indexedArguments;

        /**
         * Default {@link DatabaseTable.Builder} constructor
         */
        public Builder() {
            arguments = new ArrayList<>();
            indexedArguments = new ArrayList<>();
        }

        /**
         * Register a new argument to the constructed table
         * @param argument {@link String} that hold the new argument
         * @return {@link DatabaseTable.Builder} reference to itself
         */
        public Builder registerArgument(String argument) {
            arguments.add(argument);
            return this;
        }

        /**
         * Register a new indexed argument to the constructed table
         * @param argument @link String} that hold the new indexed arguments
         * @return {@link DatabaseTable.Builder} reference to itself
         */
        public Builder registerIndexedArguments(String argument) {
            arguments.add(argument);
            indexedArguments.add(argument);
            return this;
        }

        public Builder registerTable(DatabaseTable table) {
            arguments.addAll(table.getArguments());
            indexedArguments.addAll(table.getIndexedArguments());
            return this;
        }

        /**
         * Create a new instance of {@link DatabaseTable}
         * @return {@link DatabaseTable} created from the configuration given above
         */
        public DatabaseTable create() {
            return new DatabaseTable(arguments.stream().distinct().collect(Collectors.toList()),
                    indexedArguments.stream().distinct().collect(Collectors.toList()));
        }
    }
}
