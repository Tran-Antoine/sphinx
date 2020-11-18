package net.starpye.quiz.discordimpl.user;

import discord4j.common.util.Snowflake;
import net.starype.quiz.api.game.player.Player;

public class DiscordPlayer extends Player<Snowflake> {

    public DiscordPlayer(Snowflake id, String userName, String nickName) {
        super(id, userName, nickName);
    }
}
