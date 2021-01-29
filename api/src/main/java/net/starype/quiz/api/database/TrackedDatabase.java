package net.starype.quiz.api.database;

import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrackedDatabase extends SimpleDatabase {

    private final FileParser fileParser;
    private final Set<? extends String> trackedFiles;
    private boolean hasBeenSync;

    public TrackedDatabase(DatabaseTable table, SerializedIO serializedIO, Set<? extends String> trackedFiles,
                           FileParser fileParser, boolean standalone) {
        super(new DatabaseTable.Builder()
                .registerTable(table)
                .registerIndexedArguments("file")
                .registerArgument("checksum")
                .create(), serializedIO);
        this.trackedFiles = trackedFiles;
        this.fileParser = fileParser;
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
        entriesRemoveIf(entry -> {
            String file = entry.get("file").orElseThrow();
            return !file.isBlank() && trackedFiles.contains(file);
        });

        // Detect all the changes with the tracked files
        List<? extends DatabaseEntry> entries = getEntries();
        Set<String> changeRequired = trackedFiles.stream().filter(file -> doesRequireSync(file, entries)).collect(Collectors.toSet());

        // Parse all the files that require parsing
        changeRequired.forEach(file -> sync(file, entries));
        releaseLock();

        // Write the new db
        write();
    }

    public void sync(String file, List<? extends DatabaseEntry> entries) {
        // Compute the checksum of the file
        CheckSum checkSum = fileParser.computeChecksum(file).orElseThrow();

        // Update the entries (remove all the old instance of the file)
        entriesRemoveIf(entry -> entry.get("file").orElseThrow().equals(file));

        // Updating the entries presents in the file
        Set<DatabaseEntry> newEntries = fileParser.read(file, this);
        newEntries.forEach(entry -> {
            entry.set("file", file);
            entry.set("checksum", new String(checkSum.rawData().array()));
        });
    }

    private boolean doesRequireSync(String file, List<? extends DatabaseEntry> entries) {
        return entries.stream()
                .filter(entry -> entry.get("file").orElseThrow().equals(file))
                .noneMatch(entry -> CheckSum.fromRawCheckSum(ByteBuffer.wrap(entry.get("checksum").orElseThrow().getBytes()))
                        .equals(fileParser.computeChecksum(file).orElseThrow()));
    }

    public static class Builder {
        private DatabaseTable table;
        private FileParser fileParser;
        private SerializedIO serializedIO;
        private Set<? extends String> trackedFiles;
        private boolean standalone = false;

        public Builder setTable(DatabaseTable table) {
            this.table = table;
            return this;
        }

        public Builder setFileParser(FileParser fileParser) {
            this.fileParser = fileParser;
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

        public Builder setTrackedFiles(Set<? extends String> trackedFiles) {
            this.trackedFiles = trackedFiles;
            return this;
        }

        public Builder setTrackedDirectory(String directory) {
            this.trackedFiles = FileUtils.recursiveListAllFiles(new File(directory))
                    .stream().map(File::getPath).collect(Collectors.toSet());
            return this;
        }

        public TrackedDatabase create() {
            return new TrackedDatabase(table, serializedIO, trackedFiles, fileParser, standalone);
        }
    }

}
