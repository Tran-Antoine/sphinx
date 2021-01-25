package net.starype.quiz.api.util;

import java.security.KeyPair;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {

    public static <T> Set<T> map(Collection<String> original, Function<String, ? extends T> mapping) {
        return original
                .stream()
                .map(mapping)
                .collect(Collectors.toSet());
    }

    public static Map<String, String> unpackMap(String original) {
        Function<String,String> filter = str -> str.replace("\\;", ";")
                .replace("\\=", "=")
                .replace("\\\\", "\\");
        Pattern valuePattern = Pattern.compile("(?<!\\\\)(?:\\\\{2})*=");
        return Arrays.stream(original.split("(?<!\\\\)(?:\\\\{2})*;"))
                .filter(str -> valuePattern.matcher(str).results().count() == 1)
                .collect(Collectors.toMap(o -> filter.apply(o.split(valuePattern.pattern())[0]),
                        o -> filter.apply(o.split(valuePattern.pattern())[1])));
    }

    public static String packMap(Map<String,String> original) {
        Function<String, String> filter = str -> str.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("=", "\\=");
        return original.keySet().stream()
            .map(key -> filter.apply(key) + "=" + filter.apply(original.get(key)))
            .reduce((s1, s2) -> s1 + ";" + s2)
            .orElse("");
    }

    public static List<String> unpack(String original) {
        return unpack(original, s -> s);
    }

    public static String pack(Collection<String> original) {
        return pack(original, s -> s);
    }

    public static <T> List<T> unpack(String original, Function<String, ? extends T> mapping) {
        return Arrays.stream(original.split("(?<!\\\\)(?:\\\\{2})*;"))
                .map(s -> s.replaceAll("\\\\;", ";").replace("\\\\", "\\"))
                .map(mapping)
                .collect(Collectors.toList());
    }

    public static <T> String pack(Collection<? extends T> original, Function<T, String> mapping) {
        return original.stream()
                .map(mapping)
                .map(s -> s.replace("\\", "\\\\").replaceAll(";", "\\\\;"))
                .reduce((s1, s2) -> s1 + ";" + s2)
                .orElse("");
    }

    public static <T> Optional<T> mapOptionalNoThrow(Optional<String> optionalString, Function<String, ? extends T> mapping) {
        try {
            return Optional.of(mapping.apply(optionalString.orElseThrow()));
        }
        catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public static String concatWithSeparator(String a, String b, String separator) {
        return (a.isBlank() || b.isBlank()) ? a + b : a + separator + b;
    }
}
