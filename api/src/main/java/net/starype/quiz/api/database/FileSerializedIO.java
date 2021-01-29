package net.starype.quiz.api.database;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileSerializedIO implements SerializedIO {

    private final String filepath;

    public FileSerializedIO(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void write(ByteBuffer buffer) {
        try {
            OutputStream outputStream = new FileOutputStream(filepath);
            outputStream.write(buffer.array());
            outputStream.close();
        } catch (IOException ignored) { }
    }

    @Override
    public Optional<ByteBuffer> read() {
        try {
            InputStream inputStream = new FileInputStream(filepath);
            return Optional.of(ByteBuffer.wrap(inputStream.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
