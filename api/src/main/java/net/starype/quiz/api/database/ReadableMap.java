package net.starype.quiz.api.database;

public interface ReadableMap {

    <T> T get(String value);
}
