package net.starype.quiz.api.parser;

import java.util.Set;

public interface QuestionQuery {
    boolean apply(Set<String> tags, String text, String difficulty, String file);
}
