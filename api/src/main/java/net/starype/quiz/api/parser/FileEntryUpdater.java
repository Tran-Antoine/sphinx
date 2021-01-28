package net.starype.quiz.api.parser;

import net.starype.quiz.api.parser.TrackedDatabase.UpdatableEntry;
import net.starype.quiz.api.util.CheckSum;

import java.util.Set;
import java.util.function.Supplier;

public class FileEntryUpdater implements UpdatableEntry {

    private final String filePath;
    private DBTable table;
    private FilePathReader input;

    public FileEntryUpdater(String filePath, DBTable table, FilePathReader input) {
        this.table = table;
        this.input = input;
        this.filePath = filePath;
    }

    @Override
    public boolean needsUpdate(Set<DBEntry> entries) {
        return entries
                .stream()
                .filter(entry -> entry.id().equals(filePath))
                .noneMatch(entry -> entry.checkSum().equals(CheckSum.fromFile(filePath).orElse(CheckSum.NIL)));
    }

    @Override
    public String getId() {
        return filePath;
    }

    @Override
    public CheckSum computeCheckSum() {
        Supplier<IllegalStateException> error = () -> new IllegalStateException("Could not generate checksum from path : " + filePath);
        return CheckSum.fromFile(filePath).orElseThrow(error);
    }

    @Override
    public Set<DBEntry> generateNewEntries() {
        return QuestionParser.getDatabaseEntries(filePath, table, input);
    }
}
