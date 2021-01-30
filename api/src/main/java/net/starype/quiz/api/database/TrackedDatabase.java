package net.starype.quiz.api.database;

import net.starype.quiz.api.util.CheckSum;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrackedDatabase extends SimpleDatabase {

    private final Collection<? extends EntryUpdater> updaters;
    private boolean hasBeenSync;

    public TrackedDatabase(Collection<? extends EntryUpdater> entries, SerializedIO serializedIO, DatabaseTable table, boolean standalone) {
        super(new DatabaseTable.Builder()
                .registerTable(table)
                .registerIndexedArguments("file")
                .registerArgument("checksum")
                .create(), serializedIO);
        this.updaters = entries;
        this.hasBeenSync = standalone;
    }

    public void sync() {
        acquireLock();

        // Try to read the database
        read();

        // If standalone
        if(hasBeenSync) return;
        hasBeenSync = true;

        // Remove all entries linked to file no longer present in the database
        removeEntriesIf(entry -> {
            String file = entry.get("file").get();
            return !file.isBlank() && updaters
                    .stream()
                    .map(EntryUpdater::getVirtualPath)
                    .noneMatch(path -> path.equals(file));
        });

        // Detect all the changes with the tracked files
        List<? extends DatabaseEntry> entries = getEntries();
        Set<EntryUpdater> changeRequired = updaters
                .stream()
                .filter(updaters -> updaters.needsUpdate(entries))
                .collect(Collectors.toSet());

        // Parse all the files that require parsing
        changeRequired.forEach(this::sync);
        releaseLock();

        // Write the new db
        write();
    }

    public void sync(EntryUpdater entryUpdater) {
        // Compute the checksum of the file
        CheckSum checkSum = entryUpdater.computeCheckSum();

        // Update the entries (remove all the old instance of the file)
        String path = entryUpdater.getVirtualPath();

        removeEntriesIf(entry -> entry.get("file").get().equals(path));

        // Updating the entries presents in the file
        Set<DatabaseEntry> newEntries = entryUpdater.generateNewEntries(this);
        newEntries.forEach(entry -> {
            entry.set("file", path);
            entry.set("checksum", new String(checkSum.rawData().array()));
        });
    }

    public static class Builder {

        private DatabaseTable table;
        private SerializedIO serializedIO;
        private Set<? extends EntryUpdater> entries;
        private boolean standalone = false;

        public Builder setTable(DatabaseTable table) {
            this.table = table;
            return this;
        }

        public Builder setStandalone(boolean standalone) {
            this.standalone = standalone;
            return this;
        }

        public Builder setSerializedIO(SerializedIO serializedIO) {
            this.serializedIO = serializedIO;
            return this;
        }

        public Builder setEntries(Set<? extends EntryUpdater> entries) {
            this.entries = entries;
            return this;
        }
        public TrackedDatabase create() {
            return new TrackedDatabase(entries, serializedIO, table, standalone);
        }
    }

}
