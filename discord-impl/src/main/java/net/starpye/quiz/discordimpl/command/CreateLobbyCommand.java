package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.LobbyList;

public class CreateLobbyCommand implements DiscordCommand {

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        Snowflake playerId = author.getId();
        TextChannel channel = context.getChannel();

        if(context.getGameList().isPlaying(playerId)) {
            channel.createMessage(author.getDisplayName()+", you are already playing a game").block();
            return;
        }

        LobbyList lobbies = context.getLobbyList();
        String lobbyId = lobbies.registerLobby(channel, playerId);
        channel.createMessage("Lobby with ID " + lobbyId+" was created").block();
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Sets up a game lobby that can be started at the author's request";
    }
}
