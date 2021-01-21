package net.starype.quiz.api.util;

import javax.management.RuntimeErrorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.*;

public class Serializer {
    private final List<? extends SerializedArgument> arguments;

    public Serializer(List<? extends  SerializedArgument> arguments) {
        this.arguments = arguments;
    }

    public Optional<Map<String, ByteBuffer>> evaluate(ByteBuffer data) {
        try {
            Map<String, ByteBuffer> output = new HashMap<>();

            for (SerializedArgument argument : arguments) {
                // First check whether the argument is variable size or not
                int size = argument.getSize()
                        .orElse(data.getInt());

                // Read the size
                byte[] outputBuffer = new byte[size];
                data.get(outputBuffer);

                // Insert the data in the output
                output.put(argument.getName(), ByteBuffer.wrap(outputBuffer));
            }

            return Optional.of(output);
        }
        catch (BufferUnderflowException e) {
            return Optional.empty();
        }
    }

    public Optional<ByteBuffer> evaluate(Map<String, ByteBuffer> data) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (SerializedArgument argument : arguments) {
                // Get the corresponding ByteBuffer
                ByteBuffer buffer = data.get(argument.getName());
                buffer.position(0); // Reset the cursor

                // First check whether the argument is variable size or not
                int size = argument.getSize()
                        .orElse(buffer.remaining());

                // Assert that the size correspond to the buffer
                if(size != buffer.remaining()) {
                    throw new RuntimeException("Trying to evaluate data that doesn't correspond to the given format");
                }

                // If everything work well then push the output stream
                stream.write(ByteBuffer.allocate(4).putInt(size).array());
            }

            // Then put the byte buffer
            return Optional.of(ByteBuffer.wrap(stream.toByteArray()));
        } catch (IOException ioException) {
            return Optional.empty();
        }
    }

    public static class Builder {
        private final List<SerializedArgument> arguments;

        public Builder() {
            this.arguments = new ArrayList<>();
        }

        public Builder registerArgument(SerializedArgument argument) {
            arguments.add(argument);
            return this;
        }

        public Serializer create() {
            return new Serializer(arguments);
        }
    }
}
