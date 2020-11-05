package net.starype.quiz.api.game.player;

import net.starype.quiz.api.game.QuestionTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Player implements UUIDHolder {
    private String username;
    private String nickname;
    private Score score;

    public Player(UUID uuid, String username, String nickname) {
        super(uuid);
        this.username = username;
        this.nickname = nickname;
        scoreByTags = new HashMap<QuestionTag, Score>();
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
        this.score.increment(increment);
    }
}
