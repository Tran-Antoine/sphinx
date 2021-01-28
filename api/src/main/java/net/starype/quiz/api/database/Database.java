package net.starype.quiz.api.database;

import java.util.Set;

public interface Database extends DatabaseEntryFactory {
    void read();
    void write();
    Set<DatabaseEntry> query(DatabaseQuery query);
}
