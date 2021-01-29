package net.starype.quiz.api.database;

import net.starype.quiz.api.parser.QuestionParser;
import net.starype.quiz.api.util.CheckSum;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class FileEntryUpdater implements EntryUpdater {

    private final String filePath;
    private DatabaseTable table;
    private FilePathReader input;

    public FileEntryUpdater(String filePath, DatabaseTable table, FilePathReader input) {
        this.table = table;
        this.input = input;
        this.filePath = filePath;
    }

    @Override
    public boolean needsUpdate(Collection<? extends DatabaseEntry> entries) {
        return entries
                .stream()
                .filter(entry -> entry.get("file").get().equals(filePath))
                .noneMatch(this::compareCheckSum);
    }

    private boolean compareCheckSum(DatabaseEntry entry) {
        byte[] bytes = entry.get("checksum").get().getBytes();
        CheckSum a = CheckSum.fromRawCheckSum(ByteBuffer.wrap(bytes));
        CheckSum b = computeCheckSum();
        return a.equals(b);
    }

    @Override
    public String getVirtualPath() {
        return filePath;
    }

    @Override
    public CheckSum computeCheckSum() {
        Supplier<IllegalStateException> error = () -> new IllegalStateException("Could not generate checksum from path : " + filePath);
        return CheckSum.fromString(input.read(filePath).orElseThrow(error));
    }

    @Override
    public Set<DatabaseEntry> generateNewEntries(DatabaseEntryFactory factory) {
        return QuestionParser.getDatabaseEntries(filePath, input, factory);
    }
}
