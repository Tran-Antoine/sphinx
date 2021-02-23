package net.starype.quiz.api.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteBufferUtils {
    public static ByteBuffer concat(final ByteBuffer... buffers) {
        final ByteBuffer combined = ByteBuffer.allocate(Arrays.stream(buffers).mapToInt(buffer -> buffer.remaining() + buffer.position()).sum());
        Arrays.stream(buffers).forEach(b -> combined.put(b.duplicate().array()));
        return combined;
    }

    public static ByteBuffer extractSubBuffer(ByteBuffer data, int rawDataSize) {
        byte[] rawDataArray = new byte[rawDataSize];
        data.get(rawDataArray);
        return ByteBuffer.wrap(rawDataArray).position(0);
    }
}
