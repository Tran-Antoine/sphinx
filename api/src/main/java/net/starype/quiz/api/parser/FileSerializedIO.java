package net.starype.quiz.api.parser;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileSerializeIO implements SerializedIO {

    private final String filepath;
    private final boolean compressed;

    public FileSerializeIO(String filepath, boolean compressed) {
        this.filepath = filepath;
        this.compressed = compressed;
    }

    @Override
    public void write(ByteBuffer buffer) {
        try {
            if(compressed) {
                GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filepath));
                outputStream.write(buffer.array());
                outputStream.finish();
                outputStream.close();
            }
            else  {
                OutputStream outputStream = new FileOutputStream(filepath);
                outputStream.write(buffer.array());
                outputStream.close();
            }
        } catch (IOException ignored) { }
    }

    @Override
    public Optional<ByteBuffer> read() {
        try {
            InputStream inputStream = compressed ?
                    (new GZIPInputStream(new FileInputStream(filepath))) :
                    (new FileInputStream(filepath));
            return Optional.of(ByteBuffer.wrap(inputStream.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
