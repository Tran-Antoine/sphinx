package net.starype.quiz.api.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {

    public static <T> Set<T> concat(Set<T> a, Set<T> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toSet());
    }

    public static <T> List<T> concat(List<T> a, List<T> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

}
