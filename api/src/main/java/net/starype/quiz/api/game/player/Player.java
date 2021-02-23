package net.starype.quiz.api.game.player;

import java.util.ArrayList;
import java.util.Collection;

public class Player<T> implements IDHolder<T>, Comparable<Player<?>> {

    private String username;
    private String nickname;
    private Score score;
    private T id;
    private Collection<Player<?>> children = new ArrayList<>();

    public Player(T id, String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.id = id;
        this.score = new Score();
    }

    public Player(T uuid, String username) {
        this(uuid, username, username);
    }

    public void addChild(Player<?> player) {
        children.add(player);
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public Score getScore() {
        return score;
    }

    public void addScore(double increment) {
        this.score.incrementScore(increment);
    }

    public T getId() {
        return id;
    }

    public Collection<Player<?>> getChildren() {
        return children;
    }

    @Override
    public int compareTo(Player<?> o) {
        return Double.compare(o.score.getPoints(), this.score.getPoints());
    }
}
