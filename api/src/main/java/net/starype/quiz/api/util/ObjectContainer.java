package net.starype.quiz.api.util;

public class ObjectContainer<T> {

    private T object;

    public ObjectContainer(T object) {
        this.object = object;
    }

    public static <S> ObjectContainer<S> emptyContainer() {
        return new ObjectContainer<>(null);
    }

    public boolean exists() {
        return object != null;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }
}
