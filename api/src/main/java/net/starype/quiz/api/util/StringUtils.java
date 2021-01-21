package net.starype.quiz.api.util;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringUtils {

    public static <T> Set<T> map(Collection<String> original, Function<String, ? extends T> mapping) {
        return original
                .stream()
                .map(mapping)
                .collect(Collectors.toSet());
    }
}
