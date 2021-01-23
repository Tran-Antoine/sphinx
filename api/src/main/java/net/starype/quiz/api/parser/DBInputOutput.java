package net.starype.quiz.api.parser;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface DBInputOutput {
    Optional<ByteBuffer> read();
    void write(ByteBuffer buffer);
}
