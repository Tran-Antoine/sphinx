package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LobbyList;

public class CommandContext {

    private MessageContext messageContext;
    private GameList gameList;
    private LobbyList lobbyList;

    public CommandContext(MessageContext messageContext, GameList gameList, LobbyList lobbyList) {
        this.messageContext = messageContext;
        this.gameList = gameList;
        this.lobbyList = lobbyList;
    }

    public TextChannel getChannel() {
        return messageContext.textChannel;
    }

    public Member getAuthor() {
        return messageContext.author;
    }

    public String[] getArgs() {
        return messageContext.args;
    }

    public GameList getGameList() {
        return gameList;
    }

    public LobbyList getLobbyList() {
        return lobbyList;
    }

    public static class MessageContext {

        private TextChannel textChannel;
        private Member author;
        private String[] args;

        public MessageContext(TextChannel textChannel, Member author, String[] args) {
            this.textChannel = textChannel;
            this.author = author;
            this.args = args;
        }
    }
}
