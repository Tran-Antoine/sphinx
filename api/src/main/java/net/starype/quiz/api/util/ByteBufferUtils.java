package net.starype.quiz.api.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteBufferUtils {
    public static ByteBuffer concat(final ByteBuffer... buffers) {
        final ByteBuffer combined = ByteBuffer.allocate(Arrays.stream(buffers).mapToInt(Buffer::remaining).sum());
        Arrays.stream(buffers).forEach(b -> combined.put(b.duplicate()));
        return combined;
    }
}
