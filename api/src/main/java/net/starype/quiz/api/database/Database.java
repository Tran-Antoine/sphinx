package net.starype.quiz.api.database;

import java.util.Optional;
import java.util.Set;

public interface Database extends DatabaseEntryFactory {

    void read();

    void write();

    Optional<DatabaseEntry> getEntryById(DatabaseId id);

    void removeEntry(DatabaseId id);

    Set<DatabaseEntry> query(DatabaseQuery query);

}
