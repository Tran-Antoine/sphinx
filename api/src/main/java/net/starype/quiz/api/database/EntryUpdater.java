package net.starype.quiz.api.database;

import net.starype.quiz.api.util.CheckSum;

import java.util.Collection;
import java.util.Set;

public interface EntryUpdater {

    boolean needsUpdate(Collection<? extends DatabaseEntry> entries);

    String getVirtualPath();

    CheckSum computeCheckSum();

    Set<DatabaseEntry> generateNewEntries(DatabaseEntryFactory factory);
}
