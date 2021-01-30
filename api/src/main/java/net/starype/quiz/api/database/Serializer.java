package net.starype.quiz.api.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Serializer {

    private static ByteBuffer serializeObject(ByteBuffer rawData) {
        ByteBuffer output = ByteBuffer.allocate(rawData.array().length + 4);
        output.putInt(rawData.array().length);
        output.put(rawData.array());
        return output;
    }

    private static <T> T deserializeObject(ByteBuffer byteBuffer, Function<ByteBuffer, T> deserializer) {
        // Read the size
        int size = byteBuffer.getInt();

        // Create a new bytebuffer based on the required data
        byte[] rawData = new byte[size];
        byteBuffer.get(rawData);

        // Then deserialize
        return deserializer.apply(ByteBuffer.wrap(rawData));
    }

    public static <T> ByteBuffer serialize(Collection<T> data, Function<T, ByteBuffer> serializer) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(ByteBuffer.allocate(4).putInt(data.size()).array());

            for (T t : data) {
                // Can use forEach here due to the exception throw by the write function
                // (must be catch in the lambda for practical reason)
                byteArrayOutputStream.write(serializeObject(serializer.apply(t)).array());
            }
            return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Cannot load the bytebuffer from the given data");
        }
    }

    public static <T> List<T> deserialize(ByteBuffer data, Function<ByteBuffer, T> deserializer) {
        // List of the object
        List<T> output = new ArrayList<>();

        int size = data.getInt();

        for (int i = 0; i < size; i++) {
            output.add(deserializeObject(data, deserializer));
        }

        return output;
    }
}
