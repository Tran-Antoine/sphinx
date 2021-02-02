package net.starype.quiz.api.database;

import net.starype.quiz.api.parser.QuestionParser;
import net.starype.quiz.api.util.CheckSum;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;

public class ByteEntryUpdater implements EntryUpdater {

    private String virtualPath;
    private CheckSum checkSum;
    private byte[] data;

    public ByteEntryUpdater(String virtualPath, byte[] data) {
        this.virtualPath = virtualPath;
        this.data = data;
    }

    @Override
    public boolean needsUpdate(Collection<? extends DatabaseEntry> entries) {
        return true;
    }

    @Override
    public String getVirtualPath() {
        return virtualPath;
    }

    @Override
    public CheckSum computeCheckSum() {
        if(checkSum == null) {
            checkSum = CheckSum.fromByteBuffer(ByteBuffer.wrap(data));
        }
        return checkSum;
    }

    @Override
    public Set<DatabaseEntry> generateNewEntries(DatabaseEntryFactory factory) {
        String text = new String(data);
        return QuestionParser.getDatabaseEntries(text, factory);
    }
}
