package net.starype.quiz.api.game;

public interface Notifier {
    void notifyObservers();
    void addObserver(Observer observer);
}
