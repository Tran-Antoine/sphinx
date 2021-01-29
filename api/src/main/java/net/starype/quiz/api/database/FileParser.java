package net.starype.quiz.api.database;

import net.starype.quiz.api.util.CheckSum;

import java.util.Optional;
import java.util.Set;

/**
 * An {@link FileParser} is an object that can parse any file and return an {@link Set} of {@link DatabaseEntry}
 * associate to the file (it enables the user to defines specific parsing methods)
 */
public interface FileParser {
    Set<DatabaseEntry> read(String file, DatabaseEntryFactory generator);
    Optional<CheckSum> computeChecksum(String file);
}
