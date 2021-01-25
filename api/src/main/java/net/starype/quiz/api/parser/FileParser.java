package net.starype.quiz.api.parser;

import java.util.Set;

/**
 * An {@link FileParser} is an object that can parse any file and return an {@link Set} of {@link DBEntry}
 * associate to the file (it enables the user to defines specific parsing methods)
 */
public interface FileParser {
    Set<DBEntry> read(String file);
}
