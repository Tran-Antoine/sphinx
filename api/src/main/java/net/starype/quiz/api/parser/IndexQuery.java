package net.starype.quiz.api.parser;

import java.util.Set;

public interface IndexQuery {
    boolean match(Set<? extends ArgumentValue<String>> arguments);
}
