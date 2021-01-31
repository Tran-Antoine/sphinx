package net.starype.quiz.api.database;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ByteSerializedIO implements SerializedIO {

    private byte[] input;
    private AtomicReference<ByteBuffer> output;

    public ByteSerializedIO(byte[] input, AtomicReference<ByteBuffer> output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public Optional<ByteBuffer> read() {
        if(input.length == 0) {
            return Optional.empty();
        }
        return Optional.of(ByteBuffer.wrap(input));
    }

    @Override
    public void write(ByteBuffer buffer) {
        output.set(buffer.duplicate());
    }
}
