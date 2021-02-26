package net.starype.quiz.discordimpl.user;

import net.starype.quiz.api.player.Player;

public class DiscordPlayer extends Player<String> {

    public DiscordPlayer(String id, String userName, String nickName) {
        super(id, userName, nickName);
    }
}
