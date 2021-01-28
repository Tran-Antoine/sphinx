package net.starype.quiz.api.database;

import net.starype.quiz.api.game.player.IDHolder;

import java.nio.ByteBuffer;
import java.util.Objects;

public class DatabaseId implements IDHolder<Integer>, Comparable<DatabaseId> {
    private final Integer id;

    public static int getSerializedSize() {
        return 4;
    }

    public static DatabaseId deserialize(ByteBuffer data) {
        return new DatabaseId(data.getInt());
    }

    public static ByteBuffer serialize(DatabaseId id) {
        return ByteBuffer.allocate(4).putInt(id.getId());
    }

    public DatabaseId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(DatabaseId o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatabaseId)) return false;
        DatabaseId that = (DatabaseId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
