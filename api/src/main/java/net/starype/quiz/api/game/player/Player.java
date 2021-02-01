package net.starype.quiz.api.game.player;

public class Player<T> implements IDHolder<T>, Comparable<Player<?>> {

    private String username;
    private String nickname;
    private Score score;
    private T id;

    public Player(T id, String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.id = id;
        this.score = new Score();
    }

    public Player(T uuid, String username) {
        this(uuid, username, username);
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

    @Override
    public int compareTo(Player<?> o) {
        return Double.compare(o.score.getPoints(), this.score.getPoints());
    }
}
