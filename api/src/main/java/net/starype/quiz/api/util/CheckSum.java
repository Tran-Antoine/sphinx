package net.starype.quiz.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

public class CheckSum {
    private ByteBuffer checkSum;

    public static final CheckSum NIL = fromByteBuffer(ByteBuffer.allocate(1));

    private CheckSum(ByteBuffer buffer, boolean isRawData) {
        if(isRawData) {
            this.checkSum = buffer;
        }
        else {
            int startingPosition = buffer.position(); // Keep track of the starting position
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(buffer);
                checkSum = ByteBuffer.wrap(digest.digest());
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Cannot use the required algorithm for digest");
                e.printStackTrace();
            }
            finally {
                buffer.position(startingPosition);
            }
        }
    }

    public static CheckSum fromRawCheckSum(ByteBuffer buffer) {
        return new CheckSum(buffer, true);
    }

    public static CheckSum fromByteBuffer(ByteBuffer buffer) {
        return new CheckSum(buffer, false);
    }

    public static CheckSum fromString(String str) {
        return fromByteBuffer(ByteBuffer.wrap(str.getBytes()));
    }

    public static Optional<CheckSum> fromFile(String filepath) {
        File file = new File(filepath);

        // If the file is readable and does exists then checksum it
        if(file.canRead() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ByteBuffer buffer = ByteBuffer.wrap(fileInputStream.readAllBytes());
                return Optional.of(fromByteBuffer(buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public ByteBuffer rawData() {
        return checkSum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckSum)) return false;
        CheckSum checkSum1 = (CheckSum) o;
        return checkSum.equals(checkSum1.checkSum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkSum);
    }
}
