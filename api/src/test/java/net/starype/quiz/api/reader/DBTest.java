package net.starype.quiz.api.reader;

import net.starype.quiz.api.parser.DBEntry;
import net.starype.quiz.api.parser.SerializableObject;
import net.starype.quiz.api.parser.DBTable;
import net.starype.quiz.api.parser.TrackedDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Optional;

public class DBTest {
    public static void main(String[] args) {
        DBTable table = new DBTable.Builder()
                .registerArgument("p1")
                .registerArgument("p2")
                .registerIndexedArguments("t1")
                .registerIndexedArguments("t2")
                .create();

        TrackedDatabase db = new TrackedDatabase.Builder()
                .setTrackedDirectory(".git/")
                .setTable(table)
                .setParser(file -> {
                    DBEntry entry = new DBEntry(table);
                    entry.set("p1", "this is p1 arguments for file " + file);
                    entry.set("p2", "Noway");
                    entry.set("t1", file);
                    entry.set("t2", "test");
                    return Collections.singleton(entry);
                })
                .setIO(new SerializableObject() {
                    @Override
                    public Optional<ByteBuffer> read() {
                        try {
                            FileInputStream fis = new FileInputStream("db.bin");
                            return Optional.of(ByteBuffer.wrap(fis.readAllBytes()));
                        } catch (IOException e) {
                            return Optional.empty();
                        }
                    }

                    @Override
                    public void write(ByteBuffer buffer) {
                        try {
                            FileOutputStream fos = new FileOutputStream("db.bin");
                            fos.write(buffer.array());
                        } catch (IOException ignored) { }
                    }
                })
                .create();
        db.sync();
    }
}
