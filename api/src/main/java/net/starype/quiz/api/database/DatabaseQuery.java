package net.starype.quiz.api.database;

import java.util.Map;

public interface DatabaseQuery {
    boolean match(Map<String, String> index);
}
