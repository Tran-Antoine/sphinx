package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrackedDatabase implements IndexDatabase {
    private final DBTable table;
    private final DBParser parser;
    private final DBInputOutput io;
    private final List<? extends String> trackedFiles;
    private Set<DBEntry> entries;
    private boolean hasBeenSync = false;

    private TrackedDatabase(DBTable table, DBParser parser, DBInputOutput io, List<? extends String> trackedFiles) {
        this.table = table;
        this.io = io;
        this.entries = new HashSet<>();
        this.parser = parser;
        this.trackedFiles = trackedFiles;
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
            ByteBuffer buffer = io.read().orElseThrow();

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

        // Secondly compare each file in the list with the Database
        // Retrieve a list of file to sync
        Set<String> syncRequired = trackedFiles.stream()
                .filter(this::doesRequireSync)
                .collect(Collectors.toSet());

        // Discard all the file no longer tracked
        entries = entries
                .stream()
                .filter(entry -> trackedFiles.stream().anyMatch(trackedFile -> trackedFile.equals(entry.file())))
                .collect(Collectors.toSet());

        // Sync each file
        syncRequired.forEach(this::sync);
        syncOut();
    }

    private void sync(String file) {
        // Compute the checksum of the file
        CheckSum checkSum = CheckSum.fromFile(file).orElseThrow();

        // Update the entries (remove all the old instance of the file)
        entries = entries.stream()
                .filter(entry -> !entry.file().equals(file))
                .collect(Collectors.toSet());

        // Adding the entries present in the file to the DB
        Set<DBEntry> newEntries = parser.read(file);
        newEntries.forEach(entry -> {
            entry.setFile(file);
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

    private boolean doesRequireSync(String file) {
        return entries.stream()
                .noneMatch(entry -> entry.file().equals(file)) ||
               entries.stream()
                       .filter(entry -> entry.file().equals(file))
                       .noneMatch(entry -> entry.checkSum().equals(CheckSum.fromFile(file).orElse(CheckSum.NIL)));
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
        private DBParser parser;
        private DBInputOutput io;
        private List<? extends String> trackedFiles;

        public Builder setTable(DBTable table) {
            this.table = table;
            return this;
        }

        public Builder setParser(DBParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder setIO(DBInputOutput io) {
            this.io = io;
            return this;
        }

        public Builder setTrackedFiles(List<? extends String> trackedFiles) {
            this.trackedFiles = trackedFiles;
            return this;
        }

        public Builder setTrackedDirectory(String directory) {
            this.trackedFiles = FileUtils.recursiveListAllFiles(new File(directory))
                    .stream().map(File::getPath).collect(Collectors.toList());
            return this;
        }

        public TrackedDatabase create() {
            return new TrackedDatabase(table, parser, io, trackedFiles);
        }
    }
}
