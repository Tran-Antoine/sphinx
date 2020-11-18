package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import net.starpye.quiz.discordimpl.user.DiscordPlayer;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.server.GameServer;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class GameLobby {

    private Set<Snowflake> playersID;
    private Queue<GameRound> rounds;

    public GameLobby() {
        this.playersID = new HashSet<>();
    }

    public void registerPlayer(Snowflake playerID) {
        playersID.add(playerID);
    }

    public void unregisterPlayer(Snowflake playerID) {
        playersID.remove(playerID);
    }

    public void openToPublic() {

    }

    public QuizGame createGame(Guild guild) {
        Set<DiscordPlayer> gamePlayers = playersID
                .stream()
                .map(id -> asPlayer(guild, id))
                .collect(Collectors.toSet());
        GameServer server = new DiscordGameServer();
        return new SimpleGame(GameRounds.DEFAULT_PRESET, gamePlayers, server);
    }

    private DiscordPlayer asPlayer(Guild guild, Snowflake id) {
        Member player = guild.getMemberById(id).block();
        String userName = player.getUsername();
        String nickName = player.getDisplayName();
        return new DiscordPlayer(id, userName, nickName);
    }
}
