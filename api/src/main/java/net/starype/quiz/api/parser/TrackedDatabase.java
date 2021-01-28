package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.CheckSum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines  an indexable database created from updatable entries that are then registered in the Database.
 * A {@link SerializedIO} object can be used to save the current state of the database into a unique binary file
 * and therefore prevent the parsing of every file every time the program is launched. Each tracked entry is automatically
 * compared to the entry present in the database and if any difference is noticed the entry is parsed again. <br>
 * <br>
 * <b>Example of usage:</b>
 * <pre>
 *     DBTable table = new DBTable.Builder()
 *                 .registerArgument("arg1")
 *                 .registerArgument("arg2")
 *                 .registerIndexedArguments("name")
 *                 .registerIndexedArguments("index")
 *                 .create();
 *      TrackedDatabase db = new TrackedDatabase.Builder().setIO(io)
 *                                   .setParser(parser)
 *                                   .setTable(table)
 *                                   .setTrackedDirectory("../path/to/tracked/directory")
 *                                   .create();
 *      db.sync();
 * </pre>
 *  For further information about the query see {@link IndexDatabase}
 */
public class TrackedDatabase implements IndexDatabase {

    private final DBTable table;
    private final SerializedIO io;
    private List<? extends UpdatableEntry> updateCheckers;
    private final boolean dbStandalone;
    private Set<DBEntry> entries;
    private boolean hasBeenSync = false;

    private TrackedDatabase(DBTable table, SerializedIO io, List<? extends UpdatableEntry> updateCheckers,
                            boolean dbStandalone) {
        this.table = table;
        this.io = io;
        this.updateCheckers = updateCheckers;
        this.entries = new HashSet<>();
        this.dbStandalone = dbStandalone;
    }

    public void sync() {
        // If the file has already been read then simply sync out
        if(hasBeenSync) {
            syncOut();
            return;
        }

        hasBeenSync = true;

        // First try to read the DB
        try {
            ByteBuffer buffer = io.read().orElse(ByteBuffer.allocateDirect(0));

            // Read the number of entries presents in the DB
            int entriesCount = buffer.getInt();

            // For each entries parse the entries
            for(int id = 0; id < entriesCount; ++id) {
                // Read the number of bytes that compose the entry
                int nbBytes = buffer.getInt();

                // Read the require number of bytes and parse the entry
                byte[] entryRaw = new byte[nbBytes];
                buffer.get(entryRaw);
                DBEntry entry = new DBEntry(table);
                entry.evaluate(ByteBuffer.wrap(entryRaw));
                entries.add(entry);
            }
        }
        catch (RuntimeException ignored) {}

        // If database is in standalone mode then simply return here
        if(dbStandalone) return;

        // Secondly compare each file in the list with the Database
        // Retrieve a list of file to sync
        Set<UpdatableEntry> syncRequired = updateCheckers.stream()
                .filter(updateChecker -> updateChecker.needsUpdate(entries))
                .collect(Collectors.toSet());

        // Discard all the files no longer tracked
        entries = entries
                .stream()
                .filter(entry -> updateCheckers
                        .stream()
                        .anyMatch(trackedFile -> trackedFile.getId().equals(entry.id())))
                .collect(Collectors.toSet());

        // Sync each file
        syncRequired.forEach(this::sync);
        syncOut();
    }

    private void sync(UpdatableEntry updatable) {
        // Compute the checksum of the file
        CheckSum checkSum = updatable.computeCheckSum();
        String path = updatable.getId();

        // Update the entries (remove all the old instance of the file)
        this.entries.removeIf(entry -> entry.id().equals(path));

        // Add the entries present in the file to the DB
        Set<DBEntry> newEntries = updatable.generateNewEntries();
        newEntries.forEach(entry -> {
            entry.setFile(path);
            entry.setCheckSum(checkSum);
        });
        entries.addAll(newEntries);
    }

    private void syncOut() {
        try {
            // First create a new output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Secondly output the number of file
            outputStream.write(ByteBuffer.allocate(4).putInt(entries.size()).array());

            // Then output each file individually
            for(DBEntry entry : entries) {
                // Compile the data of the entry
                ByteBuffer buffer = entry.save();

                // Write the ByteBuffer data
                outputStream.write(ByteBuffer.allocate(4).putInt(buffer.array().length).array());

                // Write the buffer to the stream
                outputStream.write(buffer.array());
            }

            io.write(ByteBuffer.wrap(outputStream.toByteArray()));
        }
        catch(IOException ignored) {}
    }

    @Override
    public List<DBEntry> query(IndexQuery query) {
        if(!hasBeenSync) {
            throw new RuntimeException("Cannot create queries before the synchronisation of the DB");
        }

        return entries.stream()
                .filter(entry -> query.match(entry.getIndexedArguments()))
                .collect(Collectors.toList());
    }

    public static class Builder {

        private DBTable table;
        private SerializedIO io;
        private List<? extends UpdatableEntry> updaters;
        private boolean standalone = false;

        public Builder setTable(DBTable table) {
            this.table = table;
            return this;
        }

        public Builder setStandalone(boolean standalone) {
            this.standalone = standalone;
            return this;
        }

        public Builder setIO(SerializedIO io) {
            this.io = io;
            return this;
        }

        public Builder setTrackedFiles(List<? extends UpdatableEntry> updaters) {
            this.updaters = updaters;
            return this;
        }

        public TrackedDatabase create() {
            return new TrackedDatabase(table, io, updaters, standalone);
        }
    }

    public interface UpdatableEntry {

        boolean needsUpdate(Set<DBEntry> entries);
        String getId();
        CheckSum computeCheckSum();
        Set<DBEntry> generateNewEntries();
    }
}
