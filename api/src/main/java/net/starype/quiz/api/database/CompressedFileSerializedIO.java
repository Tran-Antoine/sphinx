package net.starype.quiz.api.database;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedFileSerializedIO implements SerializedIO {

    private final String filepath;

    public CompressedFileSerializedIO(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void write(ByteBuffer buffer) {
        try {
            GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filepath));
            outputStream.write(buffer.array());
            outputStream.finish();
            outputStream.close();
        } catch (IOException ignored) { }
    }

    @Override
    public Optional<ByteBuffer> read() {
        try {
            InputStream inputStream = new GZIPInputStream(new FileInputStream(filepath));
            return Optional.of(ByteBuffer.wrap(inputStream.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
