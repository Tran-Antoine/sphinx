package net.starype.quiz.discordimpl.game;

public interface LogContainer {

    void trackMessage(String id);
    void deleteMessages();
}
