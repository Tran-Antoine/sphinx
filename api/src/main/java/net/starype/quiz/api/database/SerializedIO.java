package net.starype.quiz.api.database;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * A simple interface for Input/Output. It defines
 * an object that can be read from and write to (such as a File)
 */
public interface SerializedIO {
    /**
     * Read the object
     * @return An optional {@link ByteBuffer} that contains the content of the object (or Empty if any error occurred)
     */
    Optional<ByteBuffer> read();

    /**
     * Write content to an object
     * @param buffer the content to be written to the object111
     */
    void write(ByteBuffer buffer);
}
