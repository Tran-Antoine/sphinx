package net.starype.quiz.api.database;

public class DatabaseIdGenerator {
    private int currentId;

    public DatabaseIdGenerator() {
        currentId = Integer.MIN_VALUE;
    }

    public void registerNewId(DatabaseId id) {
        currentId = Math.max(currentId, id.getId());
    }

    public DatabaseId generateNextId() {
        return new DatabaseId(++currentId);
    }
}
