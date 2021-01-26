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

/**
 * Class {@link TrackedDatabase} defines  an IndexDatabase created from a  list of files. Each files is parsed using  an
 * {@link FileParser} and then registered in the Database. {@link SerializedIO} can then be used to save the
 * current state of  the database into a unique binary file and therefore prevent the parsing of every file every time we
 * launch the program. Each tracked file is automatically compared to the file present in the database and if any
 * difference is noticed the file is parsed again. <br>
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
    private final FileParser parser;
    private final SerializedIO io;
    private final List<? extends String> trackedFiles;
    private final boolean dbStandalone;
    private Set<DBEntry> entries;
    private boolean hasBeenSync = false;

    private TrackedDatabase(DBTable table, FileParser parser, SerializedIO io, List<? extends String> trackedFiles,
                            boolean dbStandalone) {
        this.table = table;
        this.io = io;
        this.entries = new HashSet<>();
        this.parser = parser;
        this.trackedFiles = trackedFiles;
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

        // If database is in standalone mode then simply return here
        if(dbStandalone) return;

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
        CheckSum checkSum = parser.computeChecksum(file).orElseThrow();

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
        private FileParser parser;
        private SerializedIO io;
        private List<? extends String> trackedFiles;
        private boolean standalone = false;

        public Builder setTable(DBTable table) {
            this.table = table;
            return this;
        }

        public Builder setParser(FileParser parser) {
            this.parser = parser;
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
            return new TrackedDatabase(table, parser, io, trackedFiles, standalone);
        }
    }
}
