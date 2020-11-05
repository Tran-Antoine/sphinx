package net.starype.quiz.api.game.player;

import java.util.UUID;

public class Player implements UUIDHolder {

    private String username;
    private String nickname;
    private Score score;
    private UUID uuid;

    public Player(UUID uuid, String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.uuid = uuid;
        this.score = new Score();
    }

    public Player(UUID uuid, String username) {
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

    public UUID getUUID() {
        return uuid;
    }

}
