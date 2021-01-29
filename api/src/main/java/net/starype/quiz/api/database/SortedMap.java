package net.starype.quiz.api.database;

import java.util.*;
import java.util.stream.Collectors;

public class SortedMap implements Map<String, String> {

    private final List<Entry<String, String>> elements = new ArrayList<>();

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean containsKey(Object k) {
        return elements.stream().anyMatch(a -> a.getKey().equals(k));
    }

    @Override
    public boolean containsValue(Object v) {
        return elements.stream().anyMatch(a -> a.getValue().equals(v));
    }

    @Override
    public String get(Object key) {
        return elements.stream().filter(a -> a.getKey().equals(key)).findAny().map(Entry::getValue).orElse(null);
    }

    @Override
    public String put(String key, String value) {
        if(containsKey(key)) {
            return elements.stream().filter(a -> a.getKey().equals(key)).findAny().orElseThrow().setValue(value);
        }
        else {
            elements.add(new Entry<String, String>() {
                private String v = value;

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public String getValue() {
                    return v;
                }

                @Override
                public String setValue(String value) {
                    String oldV = v;
                    v = value;
                    return oldV;
                }
            });
            return null;
        }
    }

    @Override
    public String remove(Object key) {
        elements.removeIf(a -> a.getKey().equals(key));
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public Set<String> keySet() {
        return elements.stream().map(Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public Collection<String> values() {
        return elements.stream().map(Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new HashSet<>(elements);
    }
}
